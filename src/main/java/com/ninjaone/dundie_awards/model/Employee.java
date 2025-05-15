package com.ninjaone.dundie_awards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "dundie_awards")
    private Integer dundieAwards;

    @ManyToOne
    private Organization organization;

    public Employee() {

    }

    public Employee(String firstName, String lastName, Organization organization) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
        this.dundieAwards = 0;
    }
}