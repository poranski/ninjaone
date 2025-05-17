package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.dto.ActivityDTO;
import com.ninjaone.dundie_awards.model.Activity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityServiceTest {

	@Autowired
	private ActivityService activityService;

	@Test
	void testSetAndGetActivities() {
		List<ActivityDTO> activities = activityService.getAllActivities();

        activityService.saveActivity(new ActivityDTO("test event 1", new Date()));
		activityService.saveActivity(new ActivityDTO("test event 2", new Date()));

		List<ActivityDTO> updatedActivities = activityService.getAllActivities();

		assertNotNull(activities);
		assertEquals(activities.size() + 2, updatedActivities.size(), "Expected 2 new activities");

        for(ActivityDTO activity : activities) {
            assertNotNull(activity.getOccurredAt());
            assertNotNull(activity.getEvent());
        }
	}
}
