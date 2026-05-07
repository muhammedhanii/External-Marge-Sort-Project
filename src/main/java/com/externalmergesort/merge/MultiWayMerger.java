package com.externalmergesort.merge;

import com.externalmergesort.model.Employee;
import com.externalmergesort.model.SortCriteria;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;

/**
 * Executes K-way merge across multiple sorted files using a min-heap.
 */
public class MultiWayMerger {

    /**
     * Merges sorted input files into one final sorted output file while using incremental reads.
     *
     * @param sortedFiles sorted chunk files
     * @param outputFile merged output path
     * @param criteria selected sort criteria
     * @param logger status logger
     * @return total merged records
     * @throws IOException if merge fails
     */
    public int merge(List<Path> sortedFiles, Path outputFile, SortCriteria criteria, Consumer<String> logger) throws IOException {
        if (sortedFiles.isEmpty()) {
            throw new IllegalArgumentException("No sorted files provided for merge.");
        }

        Files.createDirectories(outputFile.getParent());

        List<BufferedReader> readers = new ArrayList<>();
        PriorityQueue<HeapNode> heap = new PriorityQueue<>(Comparator.comparing(HeapNode::employee, criteria.getComparator()));

        int mergedCount = 0;

        try {
            for (int i = 0; i < sortedFiles.size(); i++) {
                BufferedReader reader = Files.newBufferedReader(sortedFiles.get(i), StandardCharsets.UTF_8);
                readers.add(reader);

                Employee first = readNextValidEmployee(reader, sortedFiles.get(i), logger);
                if (first != null) {
                    heap.offer(new HeapNode(first, i));
                }
            }

            try (BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
                while (!heap.isEmpty()) {
                    HeapNode smallest = heap.poll();
                    writer.write(smallest.employee().toCsv());
                    writer.newLine();
                    mergedCount++;

                    int sourceIndex = smallest.sourceIndex();
                    Employee next = readNextValidEmployee(readers.get(sourceIndex), sortedFiles.get(sourceIndex), logger);
                    if (next != null) {
                        heap.offer(new HeapNode(next, sourceIndex));
                    }
                }
            }
        } finally {
            for (BufferedReader reader : readers) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    logger.accept("Warning: failed to close one reader cleanly.");
                }
            }
        }

        logger.accept("Multi-way merge completed. Output: " + outputFile.getFileName() + " (records=" + mergedCount + ")");
        return mergedCount;
    }

    private Employee readNextValidEmployee(BufferedReader reader, Path sourcePath, Consumer<String> logger) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            try {
                return Employee.fromCsv(line);
            } catch (RuntimeException ex) {
                logger.accept("Skipped malformed record in " + sourcePath.getFileName() + ": " + ex.getMessage());
            }
        }
        return null;
    }
}
