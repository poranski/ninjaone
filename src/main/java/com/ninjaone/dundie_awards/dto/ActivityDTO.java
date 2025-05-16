package com.ninjaone.dundie_awards.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ActivityDTO {

    private Long id;
    private LocalDateTime occurredAt;
    private String event;

    public ActivityDTO() {

    }

    public ActivityDTO(String event) {
        this.event = event;
        this.occurredAt = LocalDateTime.now();
    }

    public ActivityDTO(LocalDateTime localDateTime, String event) {
        this.occurredAt = localDateTime;
        this.event = event;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" + "id=" + id + ", occuredAt=" + occurredAt + ", event='" + event + '\'' + '}';
    }
}
