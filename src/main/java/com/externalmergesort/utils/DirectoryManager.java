package com.externalmergesort.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility for creating and validating required application directories.
 */
public final class DirectoryManager {

    private DirectoryManager() {
    }

    /**
     * Ensures all provided directories exist.
     *
     * @param directories paths to create if missing
     * @throws IOException if any directory cannot be created
     */
    public static void ensureDirectories(List<Path> directories) throws IOException {
        for (Path directory : directories) {
            Files.createDirectories(directory);
        }
    }

    /**
     * Deletes all files from a directory while preserving the directory itself.
     *
     * @param directory directory to clean
     * @throws IOException if cleanup fails
     */
    public static void cleanDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            return;
        }

        try (var stream = Files.list(directory)) {
            stream.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ex) {
                            throw new RuntimeException("Failed to delete file: " + path.getFileName(), ex);
                        }
                    });
        } catch (RuntimeException ex) {
            if (ex.getCause() instanceof IOException ioException) {
                throw ioException;
            }
            throw ex;
        }
    }
}
