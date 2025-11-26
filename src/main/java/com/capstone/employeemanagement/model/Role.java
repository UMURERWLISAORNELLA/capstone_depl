package com.capstone.employeemanagement.model;

/**
 * Enumerates the different roles supported by the system.  Roles are persisted
 * with their string names in the database and used by Spring Security to
 * authorize users.  Typical roles include regular end users and administrators.
 */
public enum Role {
    USER,
    ADMIN;
}