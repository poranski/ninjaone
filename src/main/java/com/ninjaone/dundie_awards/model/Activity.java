package com.ninjaone.dundie_awards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private LocalDateTime occurredAt;

    @Column(name = "event")
    private String event;

    public Activity() { }

    public Activity(String event) {
        this.occurredAt = LocalDateTime.now();
        this.event = event;
    }

    public Activity(LocalDateTime localDateTime, String event) {
        this.occurredAt = localDateTime;
        this.event = event;
    }

}
