package com.ninjaone.dundie_awards.util;

import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntityToDTOConverterTest {

	@Autowired
	private EntityToDTOConvertor entityToDTOConvertor;

	@Test
	void convertToEmployeeDTO() {
        Organization organizationPikashu = new Organization("Pikashu");
        organizationPikashu.setId(1L);

        Employee employee = new Employee("Peter", "Poranski", organizationPikashu);

        EmployeeDTO dto = entityToDTOConvertor.getEmployeeDTO(employee);
		assertNotNull(dto, "DTO should not be null");
        assertTrue(dto instanceof EmployeeDTO, "DTO should be of type EmployeeDTO");
        assertEquals(employee.getFirstName(), dto.getFirstName(), "First name should be the same");
        assertEquals(employee.getLastName(), dto.getLastName(), "Last name should be the same");
        assertEquals(employee.getOrganization().getId(), dto.getOrganization().getId(), "Organization ID should be the same");
        assertEquals(employee.getOrganization().getName(), dto.getOrganization().getName(), "Organization name should be the same");
	}

    @Test
    void convertToEmployee() {
        OrganizationDTO organizationDTO = new OrganizationDTO(1L, "Pikashu");
        EmployeeDTO employeeDTO = new EmployeeDTO("Peter", "Poranski", organizationDTO);

        Employee employee = entityToDTOConvertor.getEmployee(employeeDTO);
        assertNotNull(employee, "DTO should not be null");
        assertTrue(employee instanceof Employee, "employee should be of type Employee");
        assertEquals(employee.getFirstName(), employeeDTO.getFirstName(), "First name should be the same");
        assertEquals(employee.getLastName(), employeeDTO.getLastName(), "Last name should be the same");
        assertEquals(employee.getOrganization().getId(), employeeDTO.getOrganization().getId(), "Organization ID should be the same");
        assertEquals(employee.getOrganization().getName(), employeeDTO.getOrganization().getName(), "Organization name should be the same");
    }

}
