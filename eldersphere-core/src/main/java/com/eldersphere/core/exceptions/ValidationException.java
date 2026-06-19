package com.eldersphere.core.exceptions;

import com.eldersphere.core.enums.ExceptionCodeEnum;
import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends ElderSphereException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(ExceptionCodeEnum.INVALID_ARGUMENT, message);
        this.fieldErrors = Map.of();
    }

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(ExceptionCodeEnum.INVALID_ARGUMENT, message);
        this.fieldErrors = fieldErrors;
    }

    public static ValidationException of(String field, String message) {
        return new ValidationException("Validation failed", Map.of(field, message));
    }
}
