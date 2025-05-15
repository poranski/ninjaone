package com.ninjaone.dundie_awards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "activities")
public class Activity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "occured_at")
    private Date occuredAt;

    @Column(name = "event")
    private String event;

    private Integer batchNumber;
    private Long employeeId;

    public Activity() { }

    public Activity(String event) {
        this.occuredAt = new Date();
        this.event = event;
    }

    public Activity(Date localDateTime, String event) {
        this.occuredAt = localDateTime;
        this.event = event;
    }

    public Activity(Date localDateTime, String event, Integer batchNumber, Long employeeId) {
        this.occuredAt = localDateTime;
        this.event = event;
        this.batchNumber = batchNumber;
        this.employeeId = employeeId;
    }
}
