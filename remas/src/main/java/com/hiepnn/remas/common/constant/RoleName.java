package com.hiepnn.remas.common.constant;

public enum RoleName {
  SUPER_ADMIN("SUPER_ADMIN"),
  ADMIN("ADMIN"),
  CUSTOMER("CUSTOMER");

  private final String value;

  RoleName(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
