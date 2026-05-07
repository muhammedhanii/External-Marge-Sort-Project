package com.externalmergesort.merge;

import com.externalmergesort.model.Employee;

/**
 * Priority queue node containing an employee record and its source file index.
 */
public record HeapNode(Employee employee, int sourceIndex) {
}
