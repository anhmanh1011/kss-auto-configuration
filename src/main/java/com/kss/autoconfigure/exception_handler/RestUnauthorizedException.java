package com.kss.autoconfigure.exception_handler;

import com.kss.autoconfigure.common.EnumCodeCommonResponse;
import com.kss.autoconfigure.common.IResponseCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class RestUnauthorizedException extends ApiException {

    public RestUnauthorizedException() {
       super(EnumCodeCommonResponse.UNAUTHORIZED);
    }

}
