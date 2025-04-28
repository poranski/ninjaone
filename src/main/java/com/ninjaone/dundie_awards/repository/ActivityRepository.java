package com.ninjaone.dundie_awards.repository;

import com.ninjaone.dundie_awards.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE activities RESTART IDENTITY", nativeQuery = true)
    void truncateAndResetId();
}
