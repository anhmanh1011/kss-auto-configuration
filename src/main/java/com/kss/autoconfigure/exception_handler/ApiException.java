package com.kss.autoconfigure.exception_handler;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kss.autoconfigure.common.IResponseCode;
import com.kss.autoconfigure.common.IResponseData;
import com.kss.autoconfigure.common.ResponseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties({"suppressed", "localizedMessage", "stackTrace","cause","detailMessage"})
public class ApiException extends Exception implements IResponseData {
    private static final long serialVersionUID = 41241234213131L;

    private final String time;

    private int code;

    private String message;

    private Object data;

    @JsonProperty("trace_id")
    private String traceId;

    public ApiException(int code, String message) {
        this.time = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.code = code;
        this.message = message;
    }

    public ApiException(IResponseCode iResponseCode) {
        this.time = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.code = iResponseCode.getCode();
        this.message = iResponseCode.getMessage();
    }

    public ApiException(IResponseCode iResponseCode, Object data) {
        this.time = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.code = iResponseCode.getCode();
        this.message = iResponseCode.getMessage();
        this.data = data;
    }

}
