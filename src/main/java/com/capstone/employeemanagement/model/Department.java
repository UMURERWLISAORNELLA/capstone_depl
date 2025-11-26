package com.capstone.employeemanagement.model;

/**
 * Represents the department to which an employee belongs.  Storing
 * departments as an enumeration allows strong typing within the domain model
 * and ensures that only predefined values are persisted.  Additional
 * departments can be added here without impacting the underlying schema.
 */
public enum Department {
    HR,
    ENGINEERING,
    SALES,
    MARKETING,
    FINANCE,
    IT,
    OTHER;
}