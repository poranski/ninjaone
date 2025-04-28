package com.ninjaone.dundie_awards.repository;

import com.ninjaone.dundie_awards.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE employees RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAndResetId();
}
