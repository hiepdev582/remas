package com.hiepnn.remas.common.constant;

public enum ItemStatus {
    AVAILABLE("AVAILABLE"), // Sẵn sàng cho thuê
    RENTED("RENTED"), // Đang cho thuê
    MAINTENANCE("MAINTENANCE"); // Bảo trì

    private final String value;

    ItemStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
