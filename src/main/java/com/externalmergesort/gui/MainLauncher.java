package com.externalmergesort.gui;

/**
 * Main entry point for launching JavaFX application from packaged jars and IDEs.
 */
public final class MainLauncher {

    private MainLauncher() {
    }

    /**
     * Launches the GUI application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        ExternalMergeSortApp.launch(ExternalMergeSortApp.class, args);
    }
}
