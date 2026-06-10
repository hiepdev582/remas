package com.hiepnn.remas.common.constant;

public enum CollateralStatus {
    NONE("NONE"), // Không cọc
    CASH("CASH"), // Cọc tiền
    ASSET("ASSET"), // Cọc đồ
    BOTH("BOTH"); // Cọc cả tiền và đồ

    private final String value;

    CollateralStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
