package com.externalmergesort.io;

import com.externalmergesort.model.Employee;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides buffered read/write operations for employee records.
 */
public class EmployeeFileManager {

    /**
     * Writes all employee records to the target file using buffered I/O.
     *
     * @param filePath destination file path
     * @param employees records to write
     * @throws IOException if writing fails
     */
    public void writeEmployees(Path filePath, List<Employee> employees) throws IOException {
        Files.createDirectories(filePath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            for (Employee employee : employees) {
                writer.write(employee.toCsv());
                writer.newLine();
            }
        }
    }

    /**
     * Reads all valid employee records from the target file.
     *
     * @param filePath source file
     * @param warningLogger logger for malformed record messages
     * @return parsed employee list
     * @throws IOException if reading fails
     */
    public List<Employee> readEmployees(Path filePath, Consumer<String> warningLogger) throws IOException {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    employees.add(Employee.fromCsv(line));
                } catch (RuntimeException ex) {
                    warningLogger.accept("Skipped malformed record at " + filePath.getFileName()
                            + ":" + lineNumber + " -> " + ex.getMessage());
                }
            }
        }
        return employees;
    }

    /**
     * Counts lines in a file without loading all records.
     *
     * @param filePath file path
     * @return record count
     * @throws IOException if reading fails
     */
    public long countRecords(Path filePath) throws IOException {
        try (var lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
            return lines.filter(line -> !line.isBlank()).count();
        }
    }
}
