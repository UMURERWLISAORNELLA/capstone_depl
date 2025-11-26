package com.capstone.employeemanagement.service;

import com.capstone.employeemanagement.dto.EmployeeRequest;
import com.capstone.employeemanagement.dto.EmployeeResponse;
import com.capstone.employeemanagement.model.Employee;
import com.capstone.employeemanagement.model.User;
import com.capstone.employeemanagement.repository.EmployeeRepository;
import com.capstone.employeemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service encapsulating business logic for managing employees.  It provides
 * operations to create, retrieve, update and delete employee records while
 * enforcing access control via the service methods invoked from controllers.
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new employee and associates the current user as the creator.
     *
     * @param request request containing employee details
     * @param creatorUsername the username of the user performing the operation
     * @return the created employee response
     */
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Creator not found"));
        Employee employee = Employee.builder()
                .name(request.getName())
                .position(request.getPosition())
                .department(request.getDepartment())
                .hireDate(request.getHireDate())
                .createdBy(creator)
                .build();
        Employee saved = employeeRepository.save(employee);
        return toEmployeeResponse(saved);
    }

    /**
     * Retrieves a page of employees.  Pagination divides large datasets into
     * smaller chunks for better performance【976320552059987†L106-L115】.
     *
     * @param pageable pagination and sorting information
     * @return page of employee responses
     */
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::toEmployeeResponse);
    }

    /**
     * Retrieves a single employee by its identifier.
     *
     * @param id employee id
     * @return employee response
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        return toEmployeeResponse(employee);
    }

    /**
     * Updates an existing employee.  If the employee does not exist a
     * {@link IllegalArgumentException} is thrown.
     *
     * @param id employee identifier
     * @param request updated data
     * @return updated employee response
     */
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        employee.setName(request.getName());
        employee.setPosition(request.getPosition());
        employee.setDepartment(request.getDepartment());
        employee.setHireDate(request.getHireDate());
        Employee saved = employeeRepository.save(employee);
        return toEmployeeResponse(saved);
    }

    /**
     * Deletes an employee by its identifier.  Does nothing if the employee
     * exists; otherwise throws an {@link IllegalArgumentException}.
     *
     * @param id employee id
     */
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    /**
     * Converts an {@link Employee} entity to its response DTO.
     *
     * @param employee entity
     * @return response DTO
     */
    private EmployeeResponse toEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .position(employee.getPosition())
                .department(employee.getDepartment())
                .hireDate(employee.getHireDate())
                .build();
    }
}