package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceTest {

	@Autowired
	private EmployeeService employeeService;

	@Test
	void testGetAllEmployees() {
		List<Employee> employees = employeeService.getAllEmployees();
		assertNotNull(employees);
	}

	@Test
	void getEmployee() throws EmployeeNotFoundException {
		Employee employee = employeeService.getEmployeeById(2L);
		assertNotNull(employee);
		assertEquals("Jane", employee.getFirstName(), "First name should be Jane");
		assertEquals("Smith", employee.getLastName(), "Last name should be Smith");
	}


	@Test
	void getEmployeeNotFound() {
		assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployeeById(100L);
		});
	}


	@Test
	void createEmployee() throws EmployeeIncompleteException, EmployeeNotFoundException {
        Employee employee = getTestEmployee();
        employeeService.createEmployee(employee);

		Employee newEmployee = employeeService.getEmployeeById(employee.getId());
		assertNotNull(newEmployee);
        assertNotNull(newEmployee.getId());
		assertEquals(employee.getFirstName(), newEmployee.getFirstName(), "First name should be the same");
		assertEquals(employee.getLastName(), newEmployee.getLastName(), "Last name should be the same");

        employeeService.deleteEmployee(employee.getId());
	}

    @Test
	void createEmployeeIncompleteData() {
		Organization organizationPikashu = new Organization("Pikashu");
		Employee employee = new Employee("Peter", null, organizationPikashu);

        assertThrows(EmployeeIncompleteException.class, () -> {
            employeeService.createEmployee(employee);
        });
	}

    @Test
    void createEmployeeBadOrganization() {
        Organization organizationPikashu = new Organization("Pikashu");
        organizationPikashu.setId(1L);
        Employee employee = new Employee("Peter", null, organizationPikashu);

        assertThrows(EmployeeIncompleteException.class, () -> {
            employeeService.createEmployee(employee);
        });
    }

    @Test
    void updateEmployee() throws EmployeeIncompleteException, EmployeeNotFoundException {
        Employee testEmployee = getTestEmployee();
        Employee employee = employeeService.createEmployee(testEmployee);

        assertNotNull(employee);
        assertNotNull(employee.getId());
        assertEquals(employee.getFirstName(), testEmployee.getFirstName(), "First name should be the same");
        assertEquals(employee.getLastName(), testEmployee.getLastName(), "Last name should be the same");

        employee.setFirstName("Rick");
        employee.setLastName("Sanchez");
        employeeService.updateEmployee(employee.getId(), employee);

        Employee changedEmployee = employeeService.getEmployeeById(employee.getId());

        assertNotNull(changedEmployee);
        assertNotNull(changedEmployee.getId());
        assertEquals(employee.getFirstName(), changedEmployee.getFirstName(), "First name should be the same");
        assertEquals(employee.getLastName(), changedEmployee.getLastName(), "Last name should be the same");

        employeeService.deleteEmployee(employee.getId());
    }

    @Test
    void deleteEmployee() throws EmployeeIncompleteException, EmployeeNotFoundException {
        Employee employee = getTestEmployee();
        Employee newEmployee = employeeService.createEmployee(employee);
        assertNotNull(newEmployee);
        employeeService.deleteEmployee(newEmployee.getId());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(newEmployee.getId());
        });
    }

    private static Employee getTestEmployee() {
        Organization organizationPikashu = new Organization("Pikashu");
        organizationPikashu.setId(1L);

        Employee employee = new Employee("Peter", "Poranski", organizationPikashu);
        return employee;
    }

}
