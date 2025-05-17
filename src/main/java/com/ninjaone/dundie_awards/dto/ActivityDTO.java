package com.ninjaone.dundie_awards.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class ActivityDTO {

    private Long id;
    private Date occurredAt;
    private String event;
    private Integer batchNumber;
    private Long employeeId;
    private boolean completed = false;

    public ActivityDTO() {
    }

    public ActivityDTO(String event, Date occurredAt) {
        this.event = event;
        this.occurredAt = occurredAt;
    }

    public ActivityDTO(Date occurredAt, String message, Integer currentBatchNumber, Long employeeId) {
        this.occurredAt = occurredAt;
        this.event = message;
        this.batchNumber = currentBatchNumber;
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" + "id=" + id + ", occurredAt=" + occurredAt + ", event='" + event + '\'' +
            ", batchNumber=" + batchNumber + ", employeeId=" + employeeId + ", completed=" + completed + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityDTO that = (ActivityDTO) o;
        return completed == that.completed && Objects.equals(id, that.id) && Objects.equals(occurredAt, that.occurredAt) && Objects.equals(event, that.event) && Objects.equals(batchNumber, that.batchNumber) && Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, occurredAt, event, batchNumber, employeeId, completed);
    }
}