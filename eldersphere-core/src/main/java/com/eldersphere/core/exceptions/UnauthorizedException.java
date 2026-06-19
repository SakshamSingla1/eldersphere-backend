package com.eldersphere.core.exceptions;

import com.eldersphere.core.enums.ExceptionCodeEnum;

public class UnauthorizedException extends ElderSphereException {

    public UnauthorizedException(String message) {
        super(ExceptionCodeEnum.UNAUTHORIZED, message);
    }

    public static UnauthorizedException of(String message) {
        return new UnauthorizedException(message);
    }
}
