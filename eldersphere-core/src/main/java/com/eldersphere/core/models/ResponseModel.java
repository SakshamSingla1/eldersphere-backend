package com.eldersphere.core.models;


import com.eldersphere.core.enums.ExceptionCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ResponseModel<T> {
    private HttpStatus status;
    private String statusMessage;
    private T data;
    private ExceptionCodeEnum exceptionCode;

    public ResponseModel(String statusMessage, T data) {
        this.statusMessage = statusMessage;
        this.data = data;
    }

    public ResponseModel(String statusMessage, T data, ExceptionCodeEnum exceptionCodeEnum) {
        this.statusMessage = statusMessage;
        this.data = data;
        this.exceptionCode = exceptionCodeEnum;
    }

    public ResponseModel<T> toSuccess() {
        this.status = HttpStatus.OK;
        return this;
    }

    public ResponseModel<T> toFailure() {
        this.status = HttpStatus.BAD_REQUEST;
        return this;
    }

    public ResponseModel<T> toInternalServerError() {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        return this;
    }

    public static <T> ResponseModel<T> success(T data) {
        return new ResponseModel<T>("Success", data);
    }

}
