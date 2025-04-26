package com.ninjaone.dundie_awards.controller;

import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import com.ninjaone.dundie_awards.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void awardEmployee_success() throws Exception {
        Long employeeId = 1L;

        doAnswer(invocation -> null).when(employeeService).giveAward(employeeId);

        mockMvc.perform(get("/employees/award/{id}", employeeId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("success"));
    }

    @Test
    void awardEmployee_employeeNotFound() throws Exception {
        Long employeeId = 1L;

        doThrow(new EmployeeNotFoundException(100L)).when(employeeService).giveAward(employeeId);

        mockMvc.perform(get("/employees/award/{id}", employeeId).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }


    @Test
    void createEmployee_success() throws Exception {
        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(8L);
        employee.setFirstName("Peter");
        employee.setLastName("Poranski");
        employee.setDundieAwards(0);
        OrganizationDTO organization = new OrganizationDTO();
        organization.setId(1L);
        organization.setName("Pikashu");
        employee.setOrganization(organization);

        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(employee);

        mockMvc.perform(post("/employees").contentType(MediaType.APPLICATION_JSON).content("{\"id\":8," +
            "\"firstName\":\"Peter\",\"lastName\":\"Poranski\",\"dundieAwards\":0,\"organization\":{\"id\":1," +
            "\"name\":\"Pikashu\"}}")).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(8L))
            .andExpect(jsonPath("$.firstName").value("Peter"))
            .andExpect(jsonPath("$.lastName").value("Poranski"))
            .andExpect(jsonPath("$.dundieAwards").value(0))
            .andExpect(jsonPath("$.organization.id").value(1))
            .andExpect(jsonPath("$.organization.name").value("Pikashu"));
    }

}
