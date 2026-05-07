package com.externalmergesort.gui;

import com.externalmergesort.model.SortCriteria;
import com.externalmergesort.performance.PerformanceMetrics;
import com.externalmergesort.service.ExternalMergeSortService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.nio.file.Path;

/**
 * JavaFX desktop application for operating and visualizing the external merge sort workflow.
 */
public class ExternalMergeSortApp extends Application {

    private ExternalMergeSortService service;

    private final TextArea consoleArea = new TextArea();
    private final Label statusLabel = new Label("Status: Ready");
    private final Label statisticsLabel = new Label("Data files: 0 | Sorted files: 0 | Output records: 0");
    private final Label chunkTimeLabel = new Label("Chunk Sort: N/A");
    private final Label mergeTimeLabel = new Label("Merge: N/A");
    private final Label totalTimeLabel = new Label("Total: N/A");
    private final ProgressBar progressBar = new ProgressBar(0);

    private final Spinner<Integer> fileCountSpinner = new Spinner<>(1, 64, ExternalMergeSortService.DEFAULT_FILE_COUNT);
    private final Spinner<Integer> recordsPerFileSpinner = new Spinner<>(1, 100_000, ExternalMergeSortService.DEFAULT_RECORDS_PER_FILE);
    private final ComboBox<SortCriteria> sortCriteriaCombo = new ComboBox<>();

    private final ToggleButton themeToggle = new ToggleButton("Dark Theme");

    @Override
    public void start(Stage stage) {
        Path projectRoot = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        service = new ExternalMergeSortService(projectRoot);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(16));

        HBox topControls = createTopControls();
        VBox centerPanel = createCenterPanel();
        VBox rightPanel = createRightPanel();

        root.setTop(topControls);
        root.setCenter(centerPanel);
        root.setRight(rightPanel);

        Scene scene = new Scene(root, 1200, 700);
        applyTheme(scene, false);

        stage.setTitle("External Merge Sort - Employee Records");
        stage.setScene(scene);
        stage.show();

        refreshStatistics();
        log("Application initialized at: " + projectRoot);
    }

    private HBox createTopControls() {
        Button generateButton = new Button("Generate Files");
        Button sortButton = new Button("Sort Files");
        Button mergeButton = new Button("Merge Files");
        Button runAllButton = new Button("Run Full Pipeline");

        generateButton.setOnAction(e -> runAsync("Generate Files", () -> {
            service.generateFiles(fileCountSpinner.getValue(), recordsPerFileSpinner.getValue(), this::log, this::setProgress);
            setStatus("Files generated successfully", Color.GREEN);
            refreshStatistics();
            return null;
        }));

        sortButton.setOnAction(e -> runAsync("Sort Files", () -> {
            long nanos = service.sortChunks(sortCriteriaCombo.getValue(), this::log, this::setProgress);
            chunkTimeLabel.setText(String.format("Chunk Sort: %.3f ms", nanos / 1_000_000.0));
            setStatus("Chunk sorting completed", Color.GREEN);
            refreshStatistics();
            return null;
        }));

        mergeButton.setOnAction(e -> runAsync("Merge Files", () -> {
            long nanos = service.mergeSortedFiles(sortCriteriaCombo.getValue(), this::log, this::setProgress);
            mergeTimeLabel.setText(String.format("Merge: %.3f ms", nanos / 1_000_000.0));
            setStatus("Merge completed", Color.GREEN);
            refreshStatistics();
            return null;
        }));

        runAllButton.setOnAction(e -> runAsync("Full Pipeline", () -> {
            PerformanceMetrics metrics = service.runFullPipeline(
                    fileCountSpinner.getValue(),
                    recordsPerFileSpinner.getValue(),
                    sortCriteriaCombo.getValue(),
                    this::log,
                    this::setProgress
            );
            updateMetrics(metrics);
            setStatus("Full pipeline finished", Color.GREEN);
            refreshStatistics();
            log(String.format("Execution summary -> chunk: %.3f ms | merge: %.3f ms | total: %.3f ms",
                    metrics.chunkSortMillis(), metrics.mergeMillis(), metrics.totalMillis()));
            return null;
        }));

        sortCriteriaCombo.getItems().addAll(SortCriteria.values());
        sortCriteriaCombo.getSelectionModel().select(SortCriteria.EMPLOYEE_ID);

        fileCountSpinner.setEditable(true);
        recordsPerFileSpinner.setEditable(true);

        themeToggle.selectedProperty().addListener((obs, oldVal, selected) -> {
            Scene scene = themeToggle.getScene();
            if (scene != null) {
                applyTheme(scene, selected);
            }
        });

        HBox controls = new HBox(10,
                new Label("Files:"), fileCountSpinner,
                new Label("Records/File:"), recordsPerFileSpinner,
                new Label("Sort By:"), sortCriteriaCombo,
                generateButton, sortButton, mergeButton, runAllButton,
                themeToggle
        );
        controls.setPadding(new Insets(8, 0, 16, 0));
        controls.setAlignment(Pos.CENTER_LEFT);
        return controls;
    }

    private VBox createCenterPanel() {
        consoleArea.setEditable(false);
        consoleArea.setWrapText(true);
        consoleArea.setPrefHeight(520);

        progressBar.setPrefWidth(780);

        VBox panel = new VBox(10,
                new Label("Console Output"),
                consoleArea,
                new Label("Progress"),
                progressBar,
                statusLabel
        );
        VBox.setVgrow(consoleArea, Priority.ALWAYS);
        return panel;
    }

    private VBox createRightPanel() {
        VBox metricsBox = new VBox(8,
                new Label("Performance Metrics"),
                chunkTimeLabel,
                mergeTimeLabel,
                totalTimeLabel,
                new Separator(),
                new Label("File Statistics"),
                statisticsLabel
        );
        metricsBox.setPadding(new Insets(10));
        metricsBox.setMinWidth(280);
        metricsBox.setStyle("-fx-background-color: #f4f6f8; -fx-background-radius: 8;");
        return metricsBox;
    }

    private <T> void runAsync(String operationName, UnsafeSupplier<T> supplier) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                Platform.runLater(() -> {
                    setStatus("Running: " + operationName + "...", Color.DODGERBLUE);
                    setProgress(0);
                });
                return supplier.get();
            }
        };

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            String message = ex == null ? "Unknown error." : ex.getMessage();
            log("Error during " + operationName + ": " + message);
            setStatus("Failed: " + operationName, Color.RED);
            showErrorDialog(operationName + " failed", message);
        });

        Thread worker = new Thread(task, "external-merge-sort-worker");
        worker.setDaemon(true);
        worker.start();
    }

    private void updateMetrics(PerformanceMetrics metrics) {
        chunkTimeLabel.setText(String.format("Chunk Sort: %.3f ms", metrics.chunkSortMillis()));
        mergeTimeLabel.setText(String.format("Merge: %.3f ms", metrics.mergeMillis()));
        totalTimeLabel.setText(String.format("Total: %.3f ms", metrics.totalMillis()));
    }

    private void refreshStatistics() {
        runAsync("Refresh Statistics", () -> {
            String stats = service.getDatasetStatistics();
            Platform.runLater(() -> statisticsLabel.setText(stats));
            return null;
        });
    }

    private void setProgress(double progress) {
        Platform.runLater(() -> progressBar.setProgress(progress));
    }

    private void setStatus(String status, Color color) {
        Platform.runLater(() -> {
            statusLabel.setText("Status: " + status);
            statusLabel.setTextFill(color);
        });
    }

    private void log(String message) {
        String output = "[LOG] " + message + System.lineSeparator();
        System.out.print(output);
        Platform.runLater(() -> consoleArea.appendText(output));
    }

    private void applyTheme(Scene scene, boolean dark) {
        if (dark) {
            scene.getRoot().setStyle("-fx-base: #2c2f33; -fx-control-inner-background: #3a3f44; "
                    + "-fx-background: #2c2f33; -fx-text-fill: #f5f5f5; -fx-font-size: 13px;");
            themeToggle.setText("Light Theme");
        } else {
            scene.getRoot().setStyle("-fx-base: #ffffff; -fx-control-inner-background: #ffffff; "
                    + "-fx-background: #ffffff; -fx-text-fill: #202020; -fx-font-size: 13px;");
            themeToggle.setText("Dark Theme");
        }
    }

    private void showErrorDialog(String header, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("External Merge Sort Error");
        alert.setHeaderText(header);
        alert.setContentText(details);
        alert.showAndWait();
    }

    /**
     * Functional interface allowing checked exceptions in async actions.
     */
    @FunctionalInterface
    private interface UnsafeSupplier<T> {
        T get() throws Exception;
    }
}
