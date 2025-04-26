package com.ninjaone.dundie_awards.exception;

import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.model.Employee;

public class EmployeeIncompleteException extends Exception{

    public EmployeeIncompleteException(Employee employee) {
        super("Incomplete data needed for employee " + employee);
    }

    public EmployeeIncompleteException(EmployeeDTO employee) {
        super("Incomplete data needed for employee " + employee);
    }
}
