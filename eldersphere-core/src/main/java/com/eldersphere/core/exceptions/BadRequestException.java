package com.eldersphere.core.exceptions;

import com.eldersphere.core.enums.ExceptionCodeEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends ElderSphereException {

    private final HttpStatus status;

    public BadRequestException(ExceptionCodeEnum exceptionCode, String message, HttpStatus status) {
        super(exceptionCode, message);
        this.status = status;
    }

    public static BadRequestException badRequest(String message) {
        return new BadRequestException(ExceptionCodeEnum.INVALID_ARGUMENT, message, HttpStatus.BAD_REQUEST);
    }

    public static BadRequestException conflict(String message) {
        return new BadRequestException(ExceptionCodeEnum.DUPLICATE_ENTRY, message, HttpStatus.CONFLICT);
    }

    public static BadRequestException duplicateEmail(String message) {
        return new BadRequestException(ExceptionCodeEnum.EMAIL_DUPLICATION, message, HttpStatus.CONFLICT);
    }

    public static BadRequestException duplicatePhone(String message) {
        return new BadRequestException(ExceptionCodeEnum.DUPLICATE_MOBILE_NUMBER, message, HttpStatus.CONFLICT);
    }

    public static BadRequestException invalidOtp(String message) {
        return new BadRequestException(ExceptionCodeEnum.INVALID_OTP, message, HttpStatus.BAD_REQUEST);
    }

    public static BadRequestException accountLocked(String message) {
        return new BadRequestException(ExceptionCodeEnum.ACCOUNT_LOCKED, message, HttpStatus.FORBIDDEN);
    }
}
