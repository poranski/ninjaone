package com.ninjaone.dundie_awards.exception;

public class EmployeeAlreadyExistsException extends Exception{

    public EmployeeAlreadyExistsException(Long id) {
        super("Employee not found with id: " + id);
    }
}
