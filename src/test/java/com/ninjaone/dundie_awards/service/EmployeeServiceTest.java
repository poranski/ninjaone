package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceTest {

	@Autowired
	private EmployeeService employeeService;

    @Autowired
    private DataLoaderService dataLoaderService;

	@Test
	void testGetAllEmployees() {
		List<EmployeeDTO> employees = employeeService.getAllEmployees();
		assertNotNull(employees);
	}

	@Test
	void getEmployee() throws EmployeeNotFoundException {
		EmployeeDTO employee = employeeService.getEmployeeById(2L);
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
        EmployeeDTO employee = getTestEmployee();
        employee = employeeService.createEmployee(employee);

		EmployeeDTO newEmployee = employeeService.getEmployeeById(employee.getId());
		assertNotNull(newEmployee);
        assertNotNull(newEmployee.getId());
		assertEquals(employee.getFirstName(), newEmployee.getFirstName(), "First name should be the same");
		assertEquals(employee.getLastName(), newEmployee.getLastName(), "Last name should be the same");

        employeeService.deleteEmployee(employee.getId());
	}

    @Test
	void createEmployeeIncompleteData() {
		OrganizationDTO organizationPikashu = new OrganizationDTO("Pikashu");
		EmployeeDTO employee = new EmployeeDTO("Peter", null, organizationPikashu);

        assertThrows(EmployeeIncompleteException.class, () -> {
            employeeService.createEmployee(employee);
        });
	}

    @Test
    void createEmployeeBadOrganization() {
        OrganizationDTO organizationPikashu = new OrganizationDTO(1L, "Pikashu");
        EmployeeDTO employee = new EmployeeDTO("Peter", null, organizationPikashu);

        assertThrows(EmployeeIncompleteException.class, () -> {
            employeeService.createEmployee(employee);
        });
    }

    @Test
    void updateEmployee() throws EmployeeIncompleteException, EmployeeNotFoundException {
        EmployeeDTO testEmployee = getTestEmployee();
        EmployeeDTO employee = employeeService.createEmployee(testEmployee);

        assertNotNull(employee);
        assertNotNull(employee.getId());
        assertEquals(employee.getFirstName(), testEmployee.getFirstName(), "First name should be the same");
        assertEquals(employee.getLastName(), testEmployee.getLastName(), "Last name should be the same");

        employee.setFirstName("Rick");
        employee.setLastName("Sanchez");
        employeeService.updateEmployee(employee.getId(), employee);

        EmployeeDTO changedEmployee = employeeService.getEmployeeById(employee.getId());

        assertNotNull(changedEmployee);
        assertNotNull(changedEmployee.getId());
        assertEquals(employee.getFirstName(), changedEmployee.getFirstName(), "First name should be the same");
        assertEquals(employee.getLastName(), changedEmployee.getLastName(), "Last name should be the same");

        employeeService.deleteEmployee(employee.getId());
    }

    @Test
    void deleteEmployee() throws EmployeeIncompleteException, EmployeeNotFoundException {
        EmployeeDTO employee = getTestEmployee();
        EmployeeDTO newEmployee = employeeService.createEmployee(employee);
        assertNotNull(newEmployee);
        employeeService.deleteEmployee(newEmployee.getId());

        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(newEmployee.getId());
        });
    }

    @PostConstruct
    public void seedDatabase() {
        dataLoaderService.populateDatabase();
    }

    private static EmployeeDTO getTestEmployee() {
        OrganizationDTO organizationPikashu = new OrganizationDTO(1L, "Pikashu");

        EmployeeDTO employee = new EmployeeDTO("Peter", "Poranski", organizationPikashu);
        return employee;
    }

}
