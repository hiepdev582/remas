package com.hiepnn.remas.common.constant;

public enum CollateralItemStatus {
    HOLDING("HOLDING"), // Đang giữ
    RETURNED("RETURNED"), // Đã trả lại khách
    FORFEITED("FORFEITED"); // Đã tịch thu do mất đồ/đền bù

    private final String value;

    CollateralItemStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
