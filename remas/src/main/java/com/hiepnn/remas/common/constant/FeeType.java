package com.hiepnn.remas.common.constant;

public enum FeeType {
    DELIVERY("DELIVERY"), // Giao đồ
    CLEANING("CLEANING"), // Vệ sinh
    DAMAGE("DAMAGE"), // Hư hại
    LATE("LATE"); // Quá hạn

    private final String value;

    FeeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
