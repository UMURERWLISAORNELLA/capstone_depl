package com.capstone.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Employee Management API.  The {@code @SpringBootApplication}
 * annotation triggers component scanning, auto‑configuration and property
 * support. Running this class will start an embedded servlet container and
 * expose the REST endpoints defined throughout the application.
 */
@SpringBootApplication
public class EmployeeManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApiApplication.class, args);
    }
}