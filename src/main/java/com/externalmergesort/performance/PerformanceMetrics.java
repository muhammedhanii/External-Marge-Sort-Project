package com.externalmergesort.performance;

/**
 * Immutable performance snapshot for a processing run.
 */
public record PerformanceMetrics(long chunkSortNanos, long mergeNanos, long totalNanos,
                                 int inputFileCount, int totalRecords) {

    /**
     * @return chunk sorting time in milliseconds.
     */
    public double chunkSortMillis() {
        return chunkSortNanos / 1_000_000.0;
    }

    /**
     * @return merge time in milliseconds.
     */
    public double mergeMillis() {
        return mergeNanos / 1_000_000.0;
    }

    /**
     * @return total processing time in milliseconds.
     */
    public double totalMillis() {
        return totalNanos / 1_000_000.0;
    }
}
