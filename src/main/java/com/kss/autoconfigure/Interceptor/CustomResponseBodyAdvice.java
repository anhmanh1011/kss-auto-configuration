package com.kss.autoconfigure.Interceptor;


import com.kss.autoconfigure.JSONUtils;
import com.kss.autoconfigure.common.ResponseData;
import lombok.extern.log4j.Log4j2;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@Log4j2
public class CustomResponseBodyAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseData) {
            ((ResponseData<?>) body).setTraceId(TraceContext.traceId());
        }
        log.debug(JSONUtils.toJsonString(body));
        return body;
    }
}