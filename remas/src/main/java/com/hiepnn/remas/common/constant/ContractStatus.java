package com.hiepnn.remas.common.constant;

public enum ContractStatus {
    RESERVED("RESERVED"), // Đã cọc giữ chỗ
    ACTIVE("ACTIVE"), // Khách đã lấy đồ và đang thuê
    COMPLETED("COMPLETED"), // Đã trả đồ xong
    CANCELLED("CANCELLED"), // Hủy đơn
    OVERDUE("OVERDUE"); // Quá hạn trả

    private final String value;

    ContractStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
