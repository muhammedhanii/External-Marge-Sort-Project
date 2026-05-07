package com.externalmergesort.performance;

/**
 * Measures execution times for pipeline steps.
 */
public class PerformanceTracker {

    private long startNanos;
    private long chunkSortNanos;
    private long mergeNanos;

    /**
     * Starts total execution timing.
     */
    public void startTotal() {
        startNanos = System.nanoTime();
    }

    /**
     * Records chunk sorting duration.
     *
     * @param nanos elapsed nanoseconds
     */
    public void setChunkSortNanos(long nanos) {
        this.chunkSortNanos = nanos;
    }

    /**
     * Records merge duration.
     *
     * @param nanos elapsed nanoseconds
     */
    public void setMergeNanos(long nanos) {
        this.mergeNanos = nanos;
    }

    /**
     * Creates metrics snapshot.
     *
     * @param inputFileCount number of processed input files
     * @param totalRecords total records handled in run
     * @return immutable metrics
     */
    public PerformanceMetrics snapshot(int inputFileCount, int totalRecords) {
        long total = System.nanoTime() - startNanos;
        return new PerformanceMetrics(chunkSortNanos, mergeNanos, total, inputFileCount, totalRecords);
    }
}
