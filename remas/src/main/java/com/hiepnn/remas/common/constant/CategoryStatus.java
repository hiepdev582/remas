package com.hiepnn.remas.common.constant;

public enum CategoryStatus {
    ACTIVE("ACTIVE"), // Đang sử dụng
    INACTIVE("INACTIVE"), // Không sử dụng
    DELETED("DELETED"); // Đã xóa

    private final String value;

    CategoryStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
