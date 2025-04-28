package com.ninjaone.dundie_awards.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


@Component
public class AwardsCache {
    private final AtomicInteger totalAwards = new AtomicInteger(0);

    public void setTotalAwards(int totalAwards) {
        this.totalAwards.set(totalAwards);
    }

    public int getTotalAwards() {
        return this.totalAwards.get();
    }

    public void addOneAward() {
        this.totalAwards.incrementAndGet();
    }

    public void clearCache() {
        this.totalAwards.set(0);
    }
}
