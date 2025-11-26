package com.capstone.employeemanagement.repository;

import com.capstone.employeemanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link Employee} entities.  Extending
 * {@link JpaRepository} provides built‑in CRUD operations and pagination
 * support.  See the Spring Data JPA guide for details on how repositories
 * automatically implement methods based on their interface definitions【607653242615892†L204-L240】.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}