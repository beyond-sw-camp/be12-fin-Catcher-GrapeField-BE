package com.example.grapefield.user.model.entity;

public enum UserRole {
  ROLE_USER, ROLE_ADMIN;

  public String getRole() {
    return this.name();
  }
}
