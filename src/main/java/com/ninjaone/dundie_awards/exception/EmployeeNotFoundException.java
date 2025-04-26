package com.ninjaone.dundie_awards.exception;

public class EmployeeNotFoundException extends Exception{

    public EmployeeNotFoundException(Long id) {
        super("Employee not found with id: " + id);
    }
}
