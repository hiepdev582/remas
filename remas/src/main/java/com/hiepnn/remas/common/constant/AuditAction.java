package com.hiepnn.remas.common.constant;

public enum AuditAction {
    CREATE_CONTRACT("CREATE_CONTRACT"), // Tạo hợp đồng
    UPDATE_CONTRACT("UPDATE_CONTRACT"), // Cập nhật hợp đồng
    CANCEL_CONTRACT("CANCEL_CONTRACT"), // Hủy hợp đồng
    CREATE_ITEM("CREATE_ITEM"), // Tạo thiết bị
    UPDATE_ITEM("UPDATE_ITEM"), // Cập nhật thiết bị
    DELETE_ITEM("DELETE_ITEM"), // Xóa thiết bị
    CREATE_CUSTOMER("CREATE_CUSTOMER"), // Tạo khách hàng
    UPDATE_CUSTOMER("UPDATE_CUSTOMER"), // Cập nhật khách hàng
    DELETE_CUSTOMER("DELETE_CUSTOMER"), // Xóa khách hàng
    LOGIN("LOGIN"); // Đăng nhập

    private final String value;

    AuditAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
