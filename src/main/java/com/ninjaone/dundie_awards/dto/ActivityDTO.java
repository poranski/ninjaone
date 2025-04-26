package com.ninjaone.dundie_awards.dto;

import java.time.LocalDateTime;

public class ActivityDTO {

    private long id;
    private LocalDateTime occuredAt;
    private String event;

    public ActivityDTO() {

    }

    public ActivityDTO(String event) {
        this.event = event;
        this.occuredAt = LocalDateTime.now();
    }

    public ActivityDTO(LocalDateTime localDateTime, String event) {
        this.occuredAt = localDateTime;
        this.event = event;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" + "id=" + id + ", occuredAt=" + occuredAt + ", event='" + event + '\'' + '}';
    }
}
