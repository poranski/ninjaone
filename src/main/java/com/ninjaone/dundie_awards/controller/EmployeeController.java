package com.ninjaone.dundie_awards.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping()
public class EmployeeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);
    private EmployeeService employeeService;



    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @ResponseBody
    @GetMapping("/employees")
    @Operation(summary = "Gets all employees")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @ResponseBody
    @PostMapping("/employees")
    @Operation(summary = "Creates an employee")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        LOGGER.info("Creating an employee [Employee: {}]", employee);
        Employee newEmployee;

        try {
            newEmployee = employeeService.createEmployee(employee);

        } catch (EmployeeIncompleteException e) {
            LOGGER.error("Incomplete Data for employee [Employee: {}]", employee);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(newEmployee);
    }

    @ResponseBody
    @GetMapping("/employees/{id}")
    @Operation(summary = "Gets an employee by ID")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        LOGGER.info("Getting an employee [Id: {}]", id);

        try {
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);

        } catch (EmployeeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "Returns an employee by id")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employeeDetails) {
        LOGGER.info("Updating an employee [Id: {}]", id);

        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok(updatedEmployee);

        } catch (EmployeeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @DeleteMapping("/employees/{id}")
    @Operation(summary = "Returns an employee by id")
    public ResponseEntity<Map<String, Boolean>> deleteEmployee(@PathVariable Long id) {
        LOGGER.info("Deleting an employee [Id: {}]", id);

        try {
            employeeService.deleteEmployee(id);

            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return ResponseEntity.ok(response);

        } catch (EmployeeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}