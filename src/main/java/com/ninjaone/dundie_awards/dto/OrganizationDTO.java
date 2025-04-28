package com.ninjaone.dundie_awards.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class OrganizationDTO {

  private Long id;
  private String name;

  public OrganizationDTO() {

  }

  public OrganizationDTO(String name) {
    this.name = name;
  }

  public OrganizationDTO(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public String toString() {
    return "OrganizationDTO{" + "id=" + id + ", name='" + name + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OrganizationDTO that = (OrganizationDTO) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }
}
