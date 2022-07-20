package com.kss.autoconfigure.common;

import com.kss.autoconfigure.exception_handler.ApiException;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorResponse {
    String time;
    Integer code;
    String message;

    public ErrorResponse(ApiException apiException) {
        this.code = apiException.getCode();
        this.message = apiException.getMessage();
        time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public ErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
        time = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
