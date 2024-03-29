package com.kss.autoconfigure.interceptor;

import com.kss.autoconfigure.JSONUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.ThreadContext;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class KssInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        LogFilterRequest logFilterRequest = new LogFilterRequest();
        logFilterRequest.setClientIp(request.getRemoteAddr());
        logFilterRequest.setUri(request.getRequestURI());
        logFilterRequest.setParameter(request.getParameterMap());
        logFilterRequest.setMethod(request.getMethod());
        logFilterRequest.setHeader(getHeadersInfo(request));

        if (StringUtils.hasLength(request.getHeader("trace_id")))
            ThreadContext.put("trace_id", request.getHeader("trace_id"));
        else ThreadContext.put("trace_id", TraceContext.traceId());

        ThreadContext.put("client_ip", request.getRemoteAddr());
        log.trace(JSONUtils.toJsonString(logFilterRequest));
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ThreadContext.clearAll();
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {

        Map<String, String> map = new HashMap<String, String>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        return map;
    }

    @Data
    class LogFilterRequest {
        String uuid;
        String uri;
        String clientIp;
        String body;
        String method;
        Map parameter;
        Map<String, String> header;

    }

}
