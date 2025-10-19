package com.gantenx.raffles.biz.consists;

public enum BizType {
    OPEN_ACCOUNT("openaccount"),
    DORMANT("dormant"),
    ONGOING_CDD("ongoing_cdd"),;

    BizType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    private final String code;

    public static BizType fromCode(String code) {
        for (BizType bizType : BizType.values()) {
            if (bizType.getCode().equals(code)) {
                return bizType;
            }
        }
        throw new IllegalArgumentException("Unknown BizType code: " + code);
    }
}
