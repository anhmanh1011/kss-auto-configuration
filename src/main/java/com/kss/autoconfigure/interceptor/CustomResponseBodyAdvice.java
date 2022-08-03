package com.kss.autoconfigure.interceptor;


import com.kss.autoconfigure.JSONUtils;
import com.kss.autoconfigure.common.IResponseData;
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
        if (body instanceof IResponseData) {
             ((IResponseData) body).setTraceId(TraceContext.traceId());
        }
        log.trace(JSONUtils.toJsonString(body));
        return body;
    }
}