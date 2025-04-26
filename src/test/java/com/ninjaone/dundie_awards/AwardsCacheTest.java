package com.ninjaone.dundie_awards;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AwardsCacheTest {

	@Autowired
	private AwardsCache awardsCache;

	@Test
	void setTotalAwardsTest() {
        awardsCache.setTotalAwards(10);
        assertEquals(10, awardsCache.getTotalAwards(), "Total awards should be 10");
	}

    @Test
    void addAwardTest() {
        int startCount = awardsCache.getTotalAwards();

        for(int i = 0; i < 100; i++) {
            awardsCache.addOneAward();
        }

        assertEquals(100 + startCount, awardsCache.getTotalAwards(), "Total awards should be 110");
    }


}
