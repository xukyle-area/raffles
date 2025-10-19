package com.gantenx.raffles.biz.consists;

public enum DataSourceType {
    KAFKA("kafka"), MYSQL("mysql");

    private final String code;

    DataSourceType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DataSourceType fromCode(String code) {
        for (DataSourceType type : DataSourceType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown DataSourceType code: " + code);
    }
}
