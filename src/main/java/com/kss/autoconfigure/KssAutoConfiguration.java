package com.kss.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = {"com.kss.autoconfigure.Interceptor", "com.kss.autoconfigure.config"}
)
public class KssAutoConfiguration {
}