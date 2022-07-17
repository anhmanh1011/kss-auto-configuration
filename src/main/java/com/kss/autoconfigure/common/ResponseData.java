package com.kss.autoconfigure.common;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.kss.autoconfigure.ex.ApiException;
import com.kss.autoconfigure.ex.RestUnauthorizedException;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Data
public class ResponseData<T> implements Serializable {

    private static final long serialVersionUID = 1234567890;

    private final String time;

    private int code;

    private String message;

    private T data;

    @JsonProperty("trace_id")
    private String traceId;

    public ResponseData() {
        this.code = 0;
        this.time =
                LocalDateTime.now()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.message = "Successful!";
    }

    public ResponseData<T> success(T data) {
        this.data = data;
        return this;
    }

    public ResponseData<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public ResponseData<T> success(T data, int code, String message) {
        this.data = data;
        this.code = code;
        this.message = message;
        return this;
    }

    public ResponseData<T> error(int code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    public ResponseData<T> error(int code, String message, T data) {
        this.data = data;
        this.code = code;
        this.message = message;
        return this;
    }

    public ResponseData<T> error(ApiException apiException) {
        this.code = apiException.getErrorCode();
        this.message = apiException.getMessage();
        return this;
    }

    public ResponseData<T> error(RestUnauthorizedException apiException) {
        this.code = apiException.getErrorCode();
        this.message = apiException.getMessage();
        return this;
    }

}
