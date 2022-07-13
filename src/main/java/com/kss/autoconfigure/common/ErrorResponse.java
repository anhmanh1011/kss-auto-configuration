package com.kss.autoconfigure.common;

import com.kss.autoconfigure.ex.ApiException;
import com.kss.autoconfigure.ex.RestUnauthorizedException;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorResponse {
    String time;
    Integer code;
    String message;


    public ErrorResponse(ApiException apiException) {
        this.code = apiException.getErrorCode();
        this.message = apiException.getMessage();
        time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public ErrorResponse(RestUnauthorizedException restUnauthorizedException) {
        this.code = restUnauthorizedException.getErrorCode();
        this.message = restUnauthorizedException.getMessage();
        time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public ErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
        time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
