package com.example.grapefield.user.model.entity;

public enum UserRole {
  ROLE_USER, ROLE_ADMIN;

  public String getRole() {
    return this.name();
  }
  public String getKoreanRole() {
      return switch (this) {
          case ROLE_USER -> "일반 회원";
          case ROLE_ADMIN -> "관리자";
      };
  }
}
