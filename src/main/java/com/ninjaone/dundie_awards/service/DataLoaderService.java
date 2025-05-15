package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.cache.AwardsCache;
import com.ninjaone.dundie_awards.model.Employee;
import com.ninjaone.dundie_awards.model.Organization;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.repository.EmployeeRepository;
import com.ninjaone.dundie_awards.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DataLoaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLoaderService.class);

    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final AwardsCache awardsCache;

    public DataLoaderService(EmployeeRepository employeeRepository, OrganizationRepository organizationRepository,
                             AwardsCache awardsCache) {
        this.awardsCache = awardsCache;
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setAwardsCache() {
        LOGGER.info("Setting awards cache");
        int totalAwards = employeeRepository.findAll().stream()
            .mapToInt(employee -> Objects.requireNonNullElse(employee.getDundieAwards(), 0))
            .sum();

        this.awardsCache.setTotalAwards(totalAwards);
        LOGGER.info("Awards cache set to {}", totalAwards);
    }

    public void populateDatabase() {
        LOGGER.info("Reseeding database");
        if (employeeRepository.count() == 0) {
            Organization organizationPikashu = new Organization("Pikashu");
            organizationRepository.save(organizationPikashu);

            employeeRepository.save(new Employee("John", "Doe", organizationPikashu));
            employeeRepository.save(new Employee("Jane", "Smith", organizationPikashu));
            employeeRepository.save(new Employee("Creed", "Braton", organizationPikashu));

            Organization organizationSquanchy = new Organization("Squanchy");
            organizationRepository.save(organizationSquanchy);

            employeeRepository.save(new Employee("Michael", "Scott", organizationSquanchy));
            employeeRepository.save(new Employee("Dwight", "Schrute", organizationSquanchy));
            employeeRepository.save(new Employee("Jim", "Halpert", organizationSquanchy));
            employeeRepository.save(new Employee("Pam", "Beesley", organizationSquanchy));
        }
    }
}
