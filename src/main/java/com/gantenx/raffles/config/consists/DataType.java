package com.gantenx.raffles.config.consists;

public enum DataType {
    KAFKA("kafka"), MYSQL("mysql");

    private final String code;

    DataType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static DataType fromCode(String code) {
        for (DataType type : DataType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown DataType code: " + code);
    }
}
