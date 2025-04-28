package com.ninjaone.dundie_awards.service;

import com.ninjaone.dundie_awards.dto.ActivityDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivityServiceTest {

	@Autowired
	private ActivityService activityService;

	@Test
	void testSetAndGetActivities() {
        activityService.saveActivity("test event");
        activityService.saveActivity("test event2");
		List<ActivityDTO> activities = activityService.getAllActivities();
		assertNotNull(activities);
		assertEquals(2, activities.size(), "Expected 2 activities");

        for(ActivityDTO activity : activities) {
            assertNotNull(activity.getOccuredAt());
            assertNotNull(activity.getEvent());
        }
	}
}
