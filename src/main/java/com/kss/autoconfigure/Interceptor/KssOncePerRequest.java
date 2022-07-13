package com.kss.autoconfigure.Interceptor;

import com.kss.autoconfigure.JSONUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class KssOncePerRequest extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    public static final String CLAIMS_CUSTODYCD = "custodyCD";

    @Override
    @Trace(operationName = "Interceptor")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasLength(authorization)) {
            try {
                String claimsCusToCd = getClaimsCusToCd(request);
                ActiveSpan.tag("user.id",claimsCusToCd);
                LogFilterRequest logFilterRequest = new LogFilterRequest();
                logFilterRequest.setClientIp(request.getRemoteAddr());
                logFilterRequest.setUri(request.getRequestURI());
                logFilterRequest.setParameter(request.getParameterMap());
                logFilterRequest.setMethod(request.getMethod());
//                logFilterRequest.setHeader(getHeadersInfo(request));
                log.info(JSONUtils.toJsonString(logFilterRequest));

                request.setAttribute(CLAIMS_CUSTODYCD, claimsCusToCd);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getClaimsCusToCd(HttpServletRequest request) throws ParseException {
        String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
        return (String) getClaimsFromToken(jwtToken).getClaim(CLAIMS_CUSTODYCD);
    }

    private JWTClaimsSet getClaimsFromToken(String token) throws  ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
        return claims;
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
        String uri;
        String clientIp;
        String method;
        Map parameter;
        Map<String, String> header;

    }


}
