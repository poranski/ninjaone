package com.ninjaone.dundie_awards.controller;

import java.util.List;
import java.util.Map;

import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
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
    private final EmployeeService employeeService;



    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @ResponseBody
    @GetMapping("/employees")
    @Operation(summary = "Gets all employees")
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @ResponseBody
    @PostMapping("/employees")
    @Operation(summary = "Creates an employee")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employee) {
        LOGGER.info("Creating an employee [Employee: {}]", employee);
        EmployeeDTO newEmployee;

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
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        LOGGER.info("Getting an employee [Id: {}]", id);

        try {
            EmployeeDTO employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);

        } catch (EmployeeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "Updates an employee by id")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDetails) {
        LOGGER.info("Updating an employee [Id: {}]", id);

        try {
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok(updatedEmployee);

        } catch (EmployeeIncompleteException e) {
            LOGGER.error("Incomplete Data for employee [Employee: {}]", employeeDetails);
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

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
            return ResponseEntity.ok(Map.of("DELETED",Boolean.TRUE));

        } catch (EmployeeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ResponseBody
    @GetMapping("/employees/award/{id}")
    @Operation(summary = "Gives an award to employee by id")
    public ResponseEntity<String> awardEmployee(@PathVariable Long id) {
        LOGGER.info("Awarding an employee [Id: {}]", id);

        try {
            employeeService.giveAward(id);
            return ResponseEntity.ok("success");

        } catch (EmployeeNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}