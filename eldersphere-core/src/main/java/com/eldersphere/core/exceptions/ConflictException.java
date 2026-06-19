package com.eldersphere.core.exceptions;

import com.eldersphere.core.enums.ExceptionCodeEnum;

public class ConflictException extends ElderSphereException {

    public ConflictException(String message) {
        super(ExceptionCodeEnum.DUPLICATE_ENTRY, message);
    }

    public static ConflictException of(String message) {
        return new ConflictException(message);
    }
}
