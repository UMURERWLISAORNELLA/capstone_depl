package com.capstone.employeemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents an employee record within the organization.  Each employee has
 * basic identifying information such as name and position, a department
 * enumerated via {@link Department}, a hire date and a reference to the user
 * who created the record.  Using JPA annotations ensures the class is mapped
 * to a relational table and its primary key is generated automatically as
 * recommended by the Spring Data JPA guide【607653242615892†L188-L195】.
 */
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private LocalDate hireDate;

    /**
     * The user who created this employee record.  This association is optional
     * and loaded lazily to avoid unnecessary joins.  The property is marked
     * with {@link JsonIgnore} to prevent infinite recursion during JSON
     * serialization.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @JsonIgnore
    private User createdBy;
}