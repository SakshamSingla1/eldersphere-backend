package com.eldersphere.core.enums;


import java.util.HashMap;
import java.util.Map;

public enum ContextTypeEnum {
    STANDARD_CREATE("STANDARD_CREATE"),
    STANDARD_UPDATE("STANDARD_UPDATE"),
    STANDARD_GET("STANDARD_GET");

    private final String value;

    private static final Map<String, ContextTypeEnum> valueToEnumMap = new HashMap<>();

    static {
        for (ContextTypeEnum status : ContextTypeEnum.values()) {
            valueToEnumMap.put(status.value, status);
        }
    }

    ContextTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContextTypeEnum fromValue(String value) {
        ContextTypeEnum status = valueToEnumMap.get(value.toUpperCase());
        if (status != null) {
            return status;
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}
