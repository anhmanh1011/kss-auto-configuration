package com.kss.autoconfigure.config;

import com.kss.autoconfigure.properties.KssSwaggerProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(value = KssSwaggerProperties.class)
public class SpringFoxConfig {

    @Autowired
    KssSwaggerProperties kssSwaggerProperties;

    @Bean
    @ConditionalOnProperty(
            name = "kss.enable.swagger",
            havingValue = "true")
    public Docket api() {
//        List<Response> responseMessages = Collections.singletonList(new Response(String.valueOf(EnumCodeResponse.INVALID_PARAM.getCode()), EnumCodeResponse.INVALID_PARAM.getMessage(), true, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));

//        HttpAuthenticationScheme authenticationScheme = HttpAuthenticationScheme
//                .JWT_BEARER_BUILDER
//                .name("jwtTOKEN")
//                .build();

        Docket docket = new Docket(DocumentationType.OAS_30).forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build().
                securitySchemes(Collections.emptyList())
                .securityContexts(Collections.singletonList(securityContext()))
                .apiInfo(apiInfo());

        return docket;
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.ant("/e/**"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("jwtTOKEN", authorizationScopes));
    }

    //    private ApiInfo apiInfo() {
//        return new ApiInfo(
//                "User management service",
//                "provider any api for account",
//                "1.0.0",
//                "https://git.kss.com.vn/projects/ACC/repos/spring-boot-keycloak/browse",
//                new Contact("DAO DUC MANH", "https://git.kss.com.vn/projects/ACC/repos/spring-boot-keycloak/browse", "manh.dd@kss.com.vn"),
//                "License of API", "https://git.kss.com.vn/projects/ACC/repos/spring-boot-keycloak/browse", Collections.emptyList());
//    }
    private ApiInfo apiInfo() {
        return new ApiInfo(
                kssSwaggerProperties.getTitle(),
                kssSwaggerProperties.getDesc(),
                kssSwaggerProperties.getVersion(),
                kssSwaggerProperties.getGitUrl(),
                new Contact(kssSwaggerProperties.getContract().getName(), kssSwaggerProperties.getContract().getUrl(), kssSwaggerProperties.getContract().getEmail()),
                kssSwaggerProperties.getLicense(), null, Collections.emptyList());
    }

    @Bean
    public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof WebMvcRequestHandlerProvider) {
                    customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
                }
                return bean;
            }

            private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
                List<T> copy = mappings.stream()
                        .filter(mapping -> mapping.getPatternParser() == null)
                        .collect(Collectors.toList());
                mappings.clear();
                mappings.addAll(copy);
            }

            @SuppressWarnings("unchecked")
            private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
                try {
                    Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                    field.setAccessible(true);
                    return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }

}
