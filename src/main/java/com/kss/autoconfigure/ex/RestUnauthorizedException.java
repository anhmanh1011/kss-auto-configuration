package com.kss.autoconfigure.ex;

import com.kss.autoconfigure.common.EnumCodeCommonResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RestUnauthorizedException extends Exception {
    int errorCode;
    String message;
    public RestUnauthorizedException() {
        this.message = EnumCodeCommonResponse.UNAUTHORIZED.getMessage();
        this.errorCode = EnumCodeCommonResponse.UNAUTHORIZED.getCode();
    }

}
