package com.ninjaone.dundie_awards.util;

import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.dto.OrganizationDTO;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class EntityToDTOConvertor {

    private final ModelMapper modelMapper;

    public EntityToDTOConvertor(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public List<EmployeeDTO> getEmployeeDTOs(List<Employee> employees) {
        List<EmployeeDTO> dto = new LinkedList<>();
        employees.forEach(employee -> dto.add(modelMapper.map(employee, EmployeeDTO.class)));
        return dto;
    }

    public EmployeeDTO getEmployeeDTO(Employee employee) {
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    public OrganizationDTO geOrganizationDTO(Organization organization) {
        return modelMapper.map(organization, OrganizationDTO.class);
    }

    public Employee getEmployee(EmployeeDTO employee) {
        return modelMapper.map(employee, Employee.class);
    }

    public Organization geOrganization(OrganizationDTO organization) {
        return modelMapper.map(organization, Organization.class);
    }


}
