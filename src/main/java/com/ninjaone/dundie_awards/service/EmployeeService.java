package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.cache.AwardsCache;
import com.ninjaone.dundie_awards.dto.ActivityDTO;
import com.ninjaone.dundie_awards.messages.MessageBroker;
import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.util.EntityToDTOConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);
    public static final String REQUESTED_EMPLOYEE_DOES_NOT_EXIST_ID = "Requested employee does not exist `[Id: {}]";

    private final MessageBroker messageBroker;
    private final AwardsCache awardsCache;
    private final EntityToDTOConvertor entityToDTOConvertor;
    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;

    public EmployeeService(EmployeeRepository employeeRepository, OrganizationRepository organizationRepository,
                           AwardsCache awardsCache, EntityToDTOConvertor entityToDTOConvertor,
                           MessageBroker messageBroker) {
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
        this.entityToDTOConvertor = entityToDTOConvertor;
        this.awardsCache = awardsCache;
        this.messageBroker = messageBroker;
    }

    public List<EmployeeDTO> getAllEmployees() {
        LOGGER.info("Getting all employees");
        return entityToDTOConvertor.getEmployeeDTOs(employeeRepository.findAll());

    }

    public EmployeeDTO getEmployeeById(Long id) throws EmployeeNotFoundException {
        LOGGER.info("Getting employee [Id: {}]", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> {
                LOGGER.info(REQUESTED_EMPLOYEE_DOES_NOT_EXIST_ID, id);
                return new EmployeeNotFoundException(id);
            });

        return entityToDTOConvertor.map(employee, EmployeeDTO.class);
    }

    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) throws EmployeeIncompleteException {
        LOGGER.debug("Creating employee. [Employee: {}]", employeeDTO);

        if (!verifyEmployeeIsComplete(employeeDTO)) {
            throw new EmployeeIncompleteException(employeeDTO);
        }

        employeeDTO.setDundieAwards(0);
        Employee employee = entityToDTOConvertor.map(employeeDTO, Employee.class);
        return entityToDTOConvertor.map(employeeRepository.save(employee), EmployeeDTO.class);
    }


    public void deleteEmployee(Long id) throws EmployeeNotFoundException {
        LOGGER.info("Deleting employee [Id: {}]", id);

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException(id));

        employeeRepository.delete(employee);
    }


    public List<EmployeeDTO> getEmployeesInOrganization(Long organizationId) {
        LOGGER.info("Getting employees in organization [Id: {}]", organizationId);
        List<Employee> employees = employeeRepository.findByOrganizationId(organizationId);
        return entityToDTOConvertor.getEmployeeDTOs(employees);
    }

    @Transactional
    public void addAwardsForOrganization(Long organizationId) {
        LOGGER.info("Adding award for everyone in org [Id: {}]", organizationId);

        List<Employee> employees = employeeRepository.findByOrganizationId(organizationId);

        employees.forEach(employee -> {
            employee.setDundieAwards(
                Optional.ofNullable(employee.getDundieAwards()).orElse(0) + 1
            );
            awardsCache.addOneAward();
        });

        employeeRepository.saveAll(employees);
        messageBroker.sendMultipleTransactionalMessages(employees);
    }


    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDetails)
        throws EmployeeNotFoundException, EmployeeIncompleteException {

        LOGGER.info("Updating employee [Employee: {}]", employeeDetails);

        if (!verifyEmployeeIsComplete(employeeDetails)) {
            LOGGER.info("Employee data is not complete [Employee: {}]", employeeDetails);
            throw new EmployeeIncompleteException(employeeDetails);
        }

        Employee employee = employeeRepository.findById(id)
            .orElseThrow(() -> {
                LOGGER.info(REQUESTED_EMPLOYEE_DOES_NOT_EXIST_ID, id);
                return new EmployeeNotFoundException(id);
            });

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());

        Employee updatedEmployee = employeeRepository.save(employee);
        return entityToDTOConvertor.map(updatedEmployee, EmployeeDTO.class);
    }


    public EmployeeDTO giveAward(Long employeeID) throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findById(employeeID)
            .orElseThrow(() -> {
                LOGGER.info(REQUESTED_EMPLOYEE_DOES_NOT_EXIST_ID, employeeID);
                return new EmployeeNotFoundException(employeeID);
            });

        employee.setDundieAwards(
            Optional.ofNullable(employee.getDundieAwards()).orElse(0) + 1
        );

        employee = employeeRepository.save(employee);

        ActivityDTO activityDTO = new ActivityDTO(
            String.format("Employee got Award!: %s %s", employee.getFirstName(), employee.getLastName()),
            new Date()
        );

        awardsCache.addOneAward();
        messageBroker.sendMessage(activityDTO);

        return entityToDTOConvertor.map(employee, EmployeeDTO.class);
    }

    private boolean verifyEmployeeIsComplete(EmployeeDTO employee) {
        if (employee == null ||
            employee.getFirstName() == null ||
            employee.getLastName() == null ||
            employee.getOrganization() == null ||
            employee.getOrganization().getId() == null) {

            LOGGER.info("Employee is not complete. [Employee: {}]", employee);
            return false;
        }

        Long orgId = employee.getOrganization().getId();
        boolean organizationExists = organizationRepository.existsById(orgId);

        if (!organizationExists) {
            LOGGER.info("Organization does not exist [Id: {}]", orgId);
            return false;
        }

        return true;
    }

}

