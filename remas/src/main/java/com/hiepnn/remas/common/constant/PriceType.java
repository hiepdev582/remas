package com.hiepnn.remas.common.constant;

public enum PriceType {
    HOURLY("HOURLY"), // Theo giờ
    DAILY("DAILY"), // Theo ngày
    WEEKLY("WEEKLY"), // Theo tuần
    MONTHLY("MONTHLY"), // Theo tháng
    WEEKEND("WEEKEND"), // Cuối tuần
    HOLIDAY("HOLIDAY"); // Ngày lễ

    private final String value;

    PriceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
