package com.hiepnn.remas.common.constant;

public enum DocumentType {
    CCCD_FRONT("CCCD_FRONT"), // Mặt trước CCCD
    CCCD_BACK("CCCD_BACK"), // Mặt sau CCCD
    DRIVER_LICENSE_FRONT("DRIVER_LICENSE_FRONT"), // Mặt trước bằng lái
    DRIVER_LICENSE_BACK("DRIVER_LICENSE_BACK"), // Mặt sau bằng lái
    OTHER("OTHER"); // Khác

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
