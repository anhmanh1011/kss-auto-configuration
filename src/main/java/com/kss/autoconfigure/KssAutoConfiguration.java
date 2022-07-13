package com.kss.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ObjectMapper.class)
@ComponentScan(
        basePackages = {"com.kss.autoconfigure.Interceptor", "com.kss.autoconfigure.config"}
)
public class KssAutoConfiguration {
}