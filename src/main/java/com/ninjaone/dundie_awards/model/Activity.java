package com.ninjaone.dundie_awards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "activities")
public class Activity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "occurred_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date occurredAt;

    @Column(name = "event")
    private String event;

    private Integer batchNumber;
    private Long employeeId;
    private boolean completed = false;

    public Activity() { }

    public Activity(String event) {
        this.occurredAt = new Date();
        this.event = event;
    }

    public Activity(Date localDateTime, String event) {
        this.occurredAt = localDateTime;
        this.event = event;
    }

    public Activity(Date localDateTime, String event, Integer batchNumber, Long employeeId) {
        this.occurredAt = localDateTime;
        this.event = event;
        this.batchNumber = batchNumber;
        this.employeeId = employeeId;
        this.completed = false;
    }

    @Override
    public String toString() {
        return "Activity{" + "id=" + id + ", occuredAt=" + occurredAt + ", event='" + event + '\'' +
            ", batchNumber=" + batchNumber + ", employeeId=" + employeeId + ", completed=" + completed + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return completed == activity.completed && Objects.equals(id, activity.id) && Objects.equals(occurredAt, activity.occurredAt) && Objects.equals(event, activity.event) && Objects.equals(batchNumber, activity.batchNumber) && Objects.equals(employeeId, activity.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, occurredAt, event, batchNumber, employeeId, completed);
    }
}
