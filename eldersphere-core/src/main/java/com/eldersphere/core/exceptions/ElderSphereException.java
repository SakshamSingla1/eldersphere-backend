package com.eldersphere.core.exceptions;

import com.eldersphere.core.enums.ExceptionCodeEnum;
import lombok.Getter;

@Getter
public class ElderSphereException extends RuntimeException {

    private final ExceptionCodeEnum exceptionCode;
    private String referenceId;

    public ElderSphereException(ExceptionCodeEnum exceptionCode, String message) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}
