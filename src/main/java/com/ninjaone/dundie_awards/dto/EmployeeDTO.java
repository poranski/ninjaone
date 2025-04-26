package com.ninjaone.dundie_awards.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class EmployeeDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private Integer dundieAwards;
    private OrganizationDTO organization;

    public EmployeeDTO() {}

    public EmployeeDTO(String firstName, String lastName, OrganizationDTO organization) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organization = organization;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" + "id=" + id + ", firstName='" + firstName + '\'' + ", lastName='" + lastName +
            '\'' + ", dundieAwards=" + dundieAwards + ", organization=" + organization + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDTO that = (EmployeeDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(dundieAwards, that.dundieAwards) && Objects.equals(organization, that.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, dundieAwards, organization);
    }
}