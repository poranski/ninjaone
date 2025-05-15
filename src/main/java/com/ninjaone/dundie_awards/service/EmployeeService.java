package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.cache.AwardsCache;
import com.ninjaone.dundie_awards.messages.MessageBroker;
import com.ninjaone.dundie_awards.dto.EmployeeDTO;
import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import com.ninjaone.dundie_awards.util.EntityToDTOConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable("employees")
    public List<EmployeeDTO> getAllEmployees() {
        LOGGER.info("Getting all employees");
        return entityToDTOConvertor.getEmployeeDTOs(employeeRepository.findAll());

    }

    @Cacheable(cacheNames = "employee", key= "#id")
    public EmployeeDTO getEmployeeById(Long id) throws EmployeeNotFoundException {
        LOGGER.info("Getting employee [Id: {}]", id);
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (optionalEmployee.isEmpty()) {
            LOGGER.info(REQUESTED_EMPLOYEE_DOES_NOT_EXIST_ID, id);
            throw new EmployeeNotFoundException(id);
        }

        return entityToDTOConvertor.map(optionalEmployee.get(), EmployeeDTO.class);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) throws EmployeeIncompleteException {
        LOGGER.debug("Creating employee. [Employee: {}]", employeeDTO);

        if (!verifyEmployeeIsComplete(employeeDTO)) {
            throw new EmployeeIncompleteException(employeeDTO);
        }

        Employee employee = entityToDTOConvertor.map(employeeDTO, Employee.class);
        return entityToDTOConvertor.map(employeeRepository.save(employee), EmployeeDTO.class);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(Long id) throws EmployeeNotFoundException {
        LOGGER.info("Deleting employee [Id: {}]", id);
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (optionalEmployee.isEmpty()) {
            throw new EmployeeNotFoundException(id);
        }

        Employee employee = optionalEmployee.get();
        employeeRepository.delete(employee);
    }

    public List<EmployeeDTO> getEmployeesInOrganization(Long organizationId) {
        LOGGER.info("Getting employees in organization [Id: {}]", organizationId);
        List<Employee> employees = employeeRepository.findByOrganizationId(organizationId);

        return entityToDTOConvertor.getEmployeeDTOs(employees);
    }

    @Transactional
    public void addAwardsForOrganization(Long id) {
        LOGGER.info("Adding award for everyone in org [Id: {}]", id);

        List<Employee> employees = employeeRepository.findByOrganizationId(id);

        for (Employee employee : employees) {
            employee.setDundieAwards(employee.getDundieAwards() + 1);
            awardsCache.addOneAward();
            employeeRepository.save(employee);
        }

        messageBroker.sendMultipleTransactionalMessages(employees);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDetails) throws EmployeeNotFoundException,
                    EmployeeIncompleteException {

        LOGGER.info("Updating employee [Employee: {}]", employeeDetails);

        if(!verifyEmployeeIsComplete(employeeDetails)) {
            LOGGER.info("Employee data is not complete [Employee: {}]", employeeDetails);
           throw new EmployeeIncompleteException(employeeDetails);
        }

        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isEmpty()) {
            LOGGER.info(REQUESTED_EMPLOYEE_DOES_NOT_EXIST_ID, id);
            throw new EmployeeNotFoundException(id);
        }

        Employee employee = optionalEmployee.get();
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());

        return entityToDTOConvertor.map(employeeRepository.save(employee), EmployeeDTO.class);
    }

    public EmployeeDTO giveAward(Long id) throws EmployeeNotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if(optionalEmployee.isEmpty()) {
            LOGGER.info(REQUESTED_EMPLOYEE_DOES_NOT_EXIST_ID, id);
            throw new EmployeeNotFoundException(id);
        }

        Employee employee = optionalEmployee.get();

        if(employee.getDundieAwards() == null) {
            employee.setDundieAwards(1);
        } else {
            employee.setDundieAwards(employee.getDundieAwards() + 1);
        }

        employee = employeeRepository.save(employee);

        Activity activity = new Activity(new Date(), "Employee got Award!: " + employee.getFirstName() +
            " " + employee.getLastName());

        awardsCache.addOneAward();
        messageBroker.sendMessage(activity);

       return entityToDTOConvertor.map(employee, EmployeeDTO.class);
    }

    private boolean verifyEmployeeIsComplete(EmployeeDTO employee) {
        if (employee.getOrganization() == null || employee.getOrganization().getId()== null ||
            employee.getFirstName() == null || employee.getLastName() == null) {

            LOGGER.info("Employee is not complete. [Employee: {}]", employee);
            return false;
        }

        Long orgId = employee.getOrganization().getId();
        Optional<Organization> optionalOrganization = organizationRepository.findById(orgId);

        if (optionalOrganization.isEmpty()) {
            LOGGER.info("Organization does not exist `[Id: {}]", orgId);
            return false;
        }

        return true;
    }
}

