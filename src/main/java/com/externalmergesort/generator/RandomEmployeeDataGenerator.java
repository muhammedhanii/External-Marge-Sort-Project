package com.externalmergesort.generator;

import com.externalmergesort.model.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates deterministic-structure random employee data.
 */
public class RandomEmployeeDataGenerator {

    private static final String[] FIRST_NAMES = {
            "Ahmed", "Sara", "Omar", "Laila", "Youssef", "Mona", "Khaled", "Nour",
            "Hassan", "Farah", "Zain", "Amina", "Mahmoud", "Rania", "Tariq", "Salma"
    };

    private static final String[] LAST_NAMES = {
            "Hassan", "Ali", "Ibrahim", "Khan", "Rahman", "Mostafa", "Saeed", "Nasser",
            "Fahmy", "Shawky", "Soliman", "Aziz", "Mansour", "Yasin", "Basim", "Naguib"
    };

    private static final String[] DEPARTMENTS = {
            "IT", "HR", "Finance", "Sales", "Marketing", "Operations", "R&D", "Support"
    };

    private final Random random;

    /**
     * Creates generator using current nanotime as random seed.
     */
    public RandomEmployeeDataGenerator() {
        this(System.nanoTime());
    }

    /**
     * Creates generator with explicit seed.
     *
     * @param seed seed for reproducible datasets
     */
    public RandomEmployeeDataGenerator(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Generates a list of random employees.
     *
     * @param startId starting employee ID (inclusive)
     * @param count number of records to generate
     * @return list of generated employees
     */
    public List<Employee> generateEmployees(int startId, int count) {
        List<Employee> employees = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int employeeId = startId + i;
            String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String department = DEPARTMENTS[random.nextInt(DEPARTMENTS.length)];
            double salary = 5000 + random.nextDouble() * 20000;

            employees.add(new Employee(employeeId, firstName, lastName, department, salary));
        }
        return employees;
    }
}
