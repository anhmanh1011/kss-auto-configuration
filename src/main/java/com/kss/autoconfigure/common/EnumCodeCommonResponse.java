package com.kss.autoconfigure.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public enum EnumCodeCommonResponse implements IResponseCode {

    INTERNAL_SERVER(500, "Internal Server Error"),
    UNAUTHORIZED(401, "UNAUTHORIAZOR"),
    NOT_FOUND(404, "NOT_FOUND"),
    INVALID_PARAM(400, "PARAM_INVALID");

    public final int code;
    public final String message;

    EnumCodeCommonResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return code + ": " + message;
    }
}
