package com.kss.autoconfigure.ex;

import com.kss.autoconfigure.common.EnumCodeResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestUnauthorizedException extends Exception {
    int errorCode;
    String message;
    public RestUnauthorizedException() {
        this.message = EnumCodeResponse.UNAUTHORIZED.getMessage();
        this.errorCode = EnumCodeResponse.UNAUTHORIZED.getCode();
    }

}
