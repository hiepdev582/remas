package com.hiepnn.remas.common.constant;

public enum CollateralType {
    CASH("CASH"), // Tiền mặt/Chuyển khoản
    IDENTITY_CARD("IDENTITY_CARD"), // CCCD
    DRIVER_LICENSE("DRIVER_LICENSE"), // Bằng lái
    MOTORBIKE("MOTORBIKE"), // Xe máy
    LAPTOP("LAPTOP"), // Laptop
    OTHER("OTHER"); // Khác

    private final String value;

    CollateralType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
