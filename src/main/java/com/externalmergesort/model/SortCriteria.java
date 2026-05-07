package com.externalmergesort.model;

import java.util.Comparator;

/**
 * Supported sort criteria for chunk sorting and merge operations.
 */
public enum SortCriteria {
    EMPLOYEE_ID("Employee ID Ascending", Employee.byEmployeeIdAscending()),
    LAST_NAME("Last Name Ascending", Employee.byLastNameAscending());

    private final String displayName;
    private final Comparator<Employee> comparator;

    SortCriteria(String displayName, Comparator<Employee> comparator) {
        this.displayName = displayName;
        this.comparator = comparator;
    }

    /**
     * @return display label suitable for UI controls.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return comparator linked to this sort criterion.
     */
    public Comparator<Employee> getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
