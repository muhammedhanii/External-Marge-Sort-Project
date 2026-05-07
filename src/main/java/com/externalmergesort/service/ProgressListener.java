package com.externalmergesort.service;

/**
 * Callback abstraction for UI progress updates.
 */
@FunctionalInterface
public interface ProgressListener {

    /**
     * Called with a fractional value from 0.0 to 1.0.
     *
     * @param progress current operation progress
     */
    void onProgress(double progress);
}
