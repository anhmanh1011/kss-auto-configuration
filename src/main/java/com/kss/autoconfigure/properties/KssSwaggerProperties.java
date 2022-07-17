package com.kss.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kss.swagger")
@Data
public class KssSwaggerProperties {
    String title;
    String body;
    String desc;
    String email;
    String version;
    String license;
    String gitUrl;
    Contract contract;

    @Data
    public static class Contract {
        String name;
        String url;
        String email;
    }

}
