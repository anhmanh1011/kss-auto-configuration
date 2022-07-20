package com.kss.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = {"com.kss.autoconfigure.interceptor", "com.kss.autoconfigure.config","com.kss.autoconfigure.exception_handler"}
)
public class KssAutoConfiguration {
}