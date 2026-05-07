package com.externalmergesort.model;

import java.util.Comparator;
import java.util.Objects;

/**
 * Represents a single employee record used in the external merge sort workflow.
 */
public class Employee {

    private int employeeId;
    private String firstName;
    private String lastName;
    private String department;
    private double salary;

    /**
     * Creates an empty employee.
     */
    public Employee() {
    }

    /**
     * Creates a fully initialized employee.
     *
     * @param employeeId employee unique identifier
     * @param firstName employee first name
     * @param lastName employee last name
     * @param department employee department
     * @param salary employee salary
     */
    public Employee(int employeeId, String firstName, String lastName, String department, double salary) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.salary = salary;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    /**
     * Converts this employee to CSV format.
     *
     * @return CSV record representation
     */
    public String toCsv() {
        return String.format("%d,%s,%s,%s,%.2f", employeeId, firstName, lastName, department, salary);
    }

    /**
     * Parses an employee from CSV.
     *
     * @param csvLine source line
     * @return parsed employee
     */
    public static Employee fromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Malformed employee record: " + csvLine);
        }
        return new Employee(
                Integer.parseInt(parts[0].trim()),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                Double.parseDouble(parts[4].trim())
        );
    }

    /**
     * @return comparator sorting by employee ID then first and last name.
     */
    public static Comparator<Employee> byEmployeeIdAscending() {
        return Comparator.comparingInt(Employee::getEmployeeId)
                .thenComparing(Employee::getLastName)
                .thenComparing(Employee::getFirstName);
    }

    /**
     * @return comparator sorting by last name then first name then employee ID.
     */
    public static Comparator<Employee> byLastNameAscending() {
        return Comparator.comparing(Employee::getLastName)
                .thenComparing(Employee::getFirstName)
                .thenComparingInt(Employee::getEmployeeId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Employee employee)) {
            return false;
        }
        return employeeId == employee.employeeId
                && Double.compare(employee.salary, salary) == 0
                && Objects.equals(firstName, employee.firstName)
                && Objects.equals(lastName, employee.lastName)
                && Objects.equals(department, employee.department);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, firstName, lastName, department, salary);
    }
}
