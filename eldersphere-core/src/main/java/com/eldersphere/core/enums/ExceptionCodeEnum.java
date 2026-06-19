package com.eldersphere.core.enums;

public enum ExceptionCodeEnum {
    UNAUTHORIZED("UNAUTHORIZED"),
    DUPLICATE_ENTRY("DUPLICATE_ENTRY"),
    NULL_POINTER("NULL_POINTER"),
    DATABASE_ERROR("DATABASE_ERROR"),
    MAPPING_ERROR("MAPPING_ERROR"),
    UNEXPECTED_ERROR("UNEXPECTED_ERROR"),
    INVALID_ARGUMENT("INVALID_ARGUMENT"),
    FORMAT_ERROR("FORMAT_ERROR"),
    DATA_NOT_FOUND("DATA_NOT_FOUND"),
    MAX_LIMIT_EXCEED("MAX_LIMIT_EXCEED"),
    FAILED_TO_UPDATE("FAILED_TO_UPDATE"),
    FAILED_TO_CREATE("FAILED_TO_CREATE"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    EMAIL_DUPLICATION("EMAIL_DUPLICATION"),
    DUPLICATE_MOBILE_NUMBER("DUPLICATE_MOBILE_NUMBER"),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    INVALID_OTP("INVALID_OTP"),
    OTP_EXPIRED("OTP_EXPIRED"),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED"),
    ACCOUNT_SUSPENDED("ACCOUNT_SUSPENDED"),
    BOOKING_CONFLICT("BOOKING_CONFLICT"),
    PROCESSING_ERROR("PROCESSING_ERROR");

    private final String value;

    ExceptionCodeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExceptionCodeEnum fromValue(String value) {
        for (ExceptionCodeEnum code : ExceptionCodeEnum.values()) {
            if (code.value.equalsIgnoreCase(value)) {
                return code;
            }
        }
        throw new IllegalArgumentException("Invalid exception code: " + value);
    }
}
