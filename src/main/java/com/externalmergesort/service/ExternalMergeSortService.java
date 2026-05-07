package com.externalmergesort.service;

import com.externalmergesort.generator.RandomEmployeeDataGenerator;
import com.externalmergesort.io.EmployeeFileManager;
import com.externalmergesort.merge.MultiWayMerger;
import com.externalmergesort.model.Employee;
import com.externalmergesort.model.SortCriteria;
import com.externalmergesort.performance.PerformanceMetrics;
import com.externalmergesort.performance.PerformanceTracker;
import com.externalmergesort.sorting.ChunkSorter;
import com.externalmergesort.utils.DirectoryManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Application service orchestrating generation, chunk sorting, and multi-way merge steps.
 */
public class ExternalMergeSortService {

    public static final int DEFAULT_FILE_COUNT = 16;
    public static final int DEFAULT_RECORDS_PER_FILE = 1000;

    private final Path rootDirectory;
    private final Path dataDirectory;
    private final Path sortedDirectory;
    private final Path outputDirectory;

    private final RandomEmployeeDataGenerator dataGenerator;
    private final EmployeeFileManager fileManager;
    private final ChunkSorter chunkSorter;
    private final MultiWayMerger merger;

    /**
     * Creates service using project root and default dependencies.
     *
     * @param rootDirectory repository root
     */
    public ExternalMergeSortService(Path rootDirectory) {
        this.rootDirectory = rootDirectory;
        this.dataDirectory = rootDirectory.resolve("data");
        this.sortedDirectory = rootDirectory.resolve("sorted");
        this.outputDirectory = rootDirectory.resolve("output");

        this.dataGenerator = new RandomEmployeeDataGenerator();
        this.fileManager = new EmployeeFileManager();
        this.chunkSorter = new ChunkSorter(fileManager);
        this.merger = new MultiWayMerger();
    }

    /**
     * Generates random input files in data directory.
     *
     * @param fileCount number of files
     * @param recordsPerFile records per file
     * @param logger logging callback
     * @param progressListener progress callback
     * @throws IOException if I/O fails
     */
    public void generateFiles(int fileCount, int recordsPerFile,
                              Consumer<String> logger,
                              ProgressListener progressListener) throws IOException {
        prepareDirectories();
        DirectoryManager.cleanDirectory(dataDirectory);

        int currentId = 1;
        for (int i = 0; i < fileCount; i++) {
            List<Employee> employees = dataGenerator.generateEmployees(currentId, recordsPerFile);
            Path filePath = dataDirectory.resolve("employees_" + (i + 1) + ".txt");
            fileManager.writeEmployees(filePath, employees);
            currentId += recordsPerFile;

            logger.accept("Generated file: " + filePath.getFileName() + " (records=" + recordsPerFile + ")");
            progressListener.onProgress((i + 1) / (double) fileCount);
        }
    }

    /**
     * Sorts all generated chunk files and writes to sorted directory.
     *
     * @param criteria sorting criteria
     * @param logger logging callback
     * @param progressListener progress callback
     * @return elapsed sorting time in nanoseconds
     * @throws IOException if I/O fails
     */
    public long sortChunks(SortCriteria criteria,
                           Consumer<String> logger,
                           ProgressListener progressListener) throws IOException {
        prepareDirectories();

        List<Path> dataFiles = listRegularFiles(dataDirectory);
        if (dataFiles.isEmpty()) {
            throw new IllegalStateException("No input files found in data directory.");
        }

        DirectoryManager.cleanDirectory(sortedDirectory);

        long start = System.nanoTime();
        for (int i = 0; i < dataFiles.size(); i++) {
            Path inputFile = dataFiles.get(i);
            Path outputFile = sortedDirectory.resolve(inputFile.getFileName().toString().replace(".txt", "_sorted.txt"));
            chunkSorter.sortChunk(inputFile, outputFile, criteria, logger);
            progressListener.onProgress((i + 1) / (double) dataFiles.size());
        }

        return System.nanoTime() - start;
    }

    /**
     * Merges all sorted files to a single final sorted output.
     *
     * @param criteria sorting criteria
     * @param logger logging callback
     * @param progressListener progress callback
     * @return elapsed merge time in nanoseconds
     * @throws IOException if I/O fails
     */
    public long mergeSortedFiles(SortCriteria criteria,
                                 Consumer<String> logger,
                                 ProgressListener progressListener) throws IOException {
        prepareDirectories();

        List<Path> sortedFiles = listRegularFiles(sortedDirectory);
        if (sortedFiles.isEmpty()) {
            throw new IllegalStateException("No sorted files found in sorted directory.");
        }

        long start = System.nanoTime();
        Path outputFile = outputDirectory.resolve("final_sorted.txt");
        merger.merge(sortedFiles, outputFile, criteria, logger);
        progressListener.onProgress(1.0);
        return System.nanoTime() - start;
    }

    /**
     * Executes full pipeline and returns complete performance metrics.
     *
     * @param fileCount number of source files
     * @param recordsPerFile records in each source file
     * @param criteria sorting criteria
     * @param logger logging callback
     * @param progressListener progress callback
     * @return metrics covering chunk sorting, merge, and total times
     * @throws IOException if any step fails
     */
    public PerformanceMetrics runFullPipeline(int fileCount, int recordsPerFile,
                                              SortCriteria criteria,
                                              Consumer<String> logger,
                                              ProgressListener progressListener) throws IOException {
        PerformanceTracker tracker = new PerformanceTracker();
        tracker.startTotal();

        logger.accept("Starting full pipeline...");
        generateFiles(fileCount, recordsPerFile, logger, progress -> progressListener.onProgress(progress * 0.30));

        long chunkSort = sortChunks(criteria, logger, progress -> progressListener.onProgress(0.30 + (progress * 0.35)));
        tracker.setChunkSortNanos(chunkSort);

        long merge = mergeSortedFiles(criteria, logger, progress -> progressListener.onProgress(0.65 + (progress * 0.35)));
        tracker.setMergeNanos(merge);

        PerformanceMetrics metrics = tracker.snapshot(fileCount, fileCount * recordsPerFile);
        logger.accept("Pipeline completed successfully.");
        return metrics;
    }

    /**
     * Builds a short dataset statistics string for UI display.
     *
     * @return statistics summary
     * @throws IOException if counting fails
     */
    public String getDatasetStatistics() throws IOException {
        long dataFiles = listRegularFiles(dataDirectory).size();
        long sortedFiles = listRegularFiles(sortedDirectory).size();
        Path outputFile = outputDirectory.resolve("final_sorted.txt");

        long outputRecords = 0;
        if (java.nio.file.Files.exists(outputFile)) {
            outputRecords = fileManager.countRecords(outputFile);
        }

        return "Data files: " + dataFiles
                + " | Sorted files: " + sortedFiles
                + " | Output records: " + outputRecords;
    }

    private void prepareDirectories() throws IOException {
        // docs directory is part of required project layout and stores screenshots/reports.
        DirectoryManager.ensureDirectories(List.of(dataDirectory, sortedDirectory, outputDirectory, rootDirectory.resolve("docs")));
    }

    private List<Path> listRegularFiles(Path directory) throws IOException {
        try (var stream = java.nio.file.Files.list(directory)) {
            return stream
                    .filter(java.nio.file.Files::isRegularFile)
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
        }
    }

    /**
     * @return absolute data directory path.
     */
    public Path getDataDirectory() {
        return dataDirectory;
    }

    /**
     * @return absolute sorted directory path.
     */
    public Path getSortedDirectory() {
        return sortedDirectory;
    }

    /**
     * @return absolute output directory path.
     */
    public Path getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Lists generated source data files.
     *
     * @return list of file paths
     * @throws IOException if listing fails
     */
    public List<Path> listDataFiles() throws IOException {
        return new ArrayList<>(listRegularFiles(dataDirectory));
    }
}
