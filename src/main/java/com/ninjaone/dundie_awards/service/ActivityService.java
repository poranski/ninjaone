package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.dto.ActivityDTO;
import com.ninjaone.dundie_awards.model.Activity;
import com.ninjaone.dundie_awards.repository.ActivityRepository;
import com.ninjaone.dundie_awards.util.EntityToDTOConvertor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityService.class);
    private final ActivityRepository activityRepository;
    private final EntityToDTOConvertor entityToDTOConvertor;

    public ActivityService(ActivityRepository activityRepository, EntityToDTOConvertor entityToDTOConvertor) {
        this.activityRepository = activityRepository;
        this.entityToDTOConvertor = entityToDTOConvertor;
    }

    @Cacheable("activities")
    public List<ActivityDTO> getAllActivities() {
        LOGGER.info("Fetching all activities");
        List<Activity> activities = activityRepository.findAll();
        return entityToDTOConvertor.getActivityDTOs(activities);
    }

    @CacheEvict(value = "activities", allEntries = true)
    public void saveActivity(Activity activity) {
        LOGGER.info("Saving activity [activity: {}}", activity);
        activityRepository.save(activity);
    }
}
