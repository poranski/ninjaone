package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.exception.EmployeeIncompleteException;
import com.ninjaone.dundie_awards.exception.EmployeeNotFoundException;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeService.class);

    /*
    private ActivityRepository activityRepository;
    private MessageBroker messageBroker;
    private AwardsCache awardsCache;
 */
    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;

    public EmployeeService(EmployeeRepository employeeRepository, OrganizationRepository organizationRepository) {
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

    /**
     * Get all employees
     *
     * @return
     */
    public List<Employee> getAllEmployees() {
        LOGGER.info("Getting all employees");
        return employeeRepository.findAll();
    }

    /**
     * Get employee by id
     *
     * @param id
     * @return
     * @throws EmployeeNotFoundException
     */
    public Employee getEmployeeById(Long id) throws EmployeeNotFoundException {
        LOGGER.info("Getting employee [Id: {}]", id);
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (!optionalEmployee.isPresent()) {
            LOGGER.info("Requested employee does not exist `[Id: {}]", id);
            throw new EmployeeNotFoundException(id);
        }

        return optionalEmployee.get();
    }

    /**
     * Create employee
     *
     * @param employee
     * @return
     */
    public Employee createEmployee(Employee employee) throws EmployeeIncompleteException {
        LOGGER.debug("Creating employee. [Employee: {}]", employee);

        if (!verifyEmployeeIsComplete(employee)) {
            throw new EmployeeIncompleteException(employee);
        }

        return employeeRepository.save(employee);
    }

    /**
     * Delete employee by id
     *
     * @param id
     * @return
     * @throws EmployeeNotFoundException
     */
    public void deleteEmployee(Long id) throws EmployeeNotFoundException {
        LOGGER.info("Deleting employee [Id: {}]", id);
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);

        if (!optionalEmployee.isPresent()) {
            throw new EmployeeNotFoundException(id);
        }

        Employee employee = optionalEmployee.get();
        employeeRepository.delete(employee);
    }

    /**
     * Update employee
     *
     * @param employeeDetails
     * @return
     * @throws EmployeeNotFoundException
     */
    public Employee updateEmployee(Long id, Employee employeeDetails) throws EmployeeNotFoundException {
        LOGGER.info("Updating employee [Employee: {}]", employeeDetails);
        // verify employee has alll required data

        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (!optionalEmployee.isPresent()) {
            LOGGER.info("Requested employee does not exist `[Id: {}]", id);
            throw new EmployeeNotFoundException(id);
        }

        Employee employee = optionalEmployee.get();
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());

        return employeeRepository.save(employee);
    }

    private boolean verifyEmployeeIsComplete(Employee employee) {
        if (employee.getOrganization() == null || employee.getOrganization().getId()== null || employee.getFirstName() == null ||
            employee.getLastName() == null) {

            LOGGER.info("Employee is not complete. [Employee: {}]", employee);
            return false;
        }

        Long orgId = employee.getOrganization().getId();
        Optional<Organization> optionalOrganization = organizationRepository.findById(orgId);

        if (!optionalOrganization.isPresent()) {
            LOGGER.info("Organization does not exist `[Id: {}]", orgId);
            return false;
        }

        return true;
    }
}

