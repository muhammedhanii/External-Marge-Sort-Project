package com.externalmergesort.sorting;

import com.externalmergesort.io.EmployeeFileManager;
import com.externalmergesort.model.Employee;
import com.externalmergesort.model.SortCriteria;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * Sorts individual employee chunk files in memory and writes sorted chunks.
 */
public class ChunkSorter {

    private final EmployeeFileManager fileManager;

    /**
     * @param fileManager file operations dependency
     */
    public ChunkSorter(EmployeeFileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Reads a chunk file, sorts it in memory, and writes sorted output.
     *
     * @param inputFile source chunk file
     * @param outputFile destination sorted chunk file
     * @param criteria selected sort criteria
     * @param logger output logger for status messages
     * @return number of sorted records
     * @throws IOException if file operations fail
     */
    public int sortChunk(Path inputFile, Path outputFile, SortCriteria criteria, Consumer<String> logger) throws IOException {
        List<Employee> employees = fileManager.readEmployees(inputFile, logger);
        employees.sort(criteria.getComparator());
        fileManager.writeEmployees(outputFile, employees);
        logger.accept("Sorted " + inputFile.getFileName() + " -> " + outputFile.getFileName()
                + " (records=" + employees.size() + ")");
        return employees.size();
    }
}
