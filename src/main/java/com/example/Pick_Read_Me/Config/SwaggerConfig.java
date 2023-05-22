package com.example.Pick_Read_Me.Config;

import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.lang.reflect.Parameter;
import java.util.*;

@Configuration
public class SwaggerConfig {
    private String version = "V0.1";
    private static final String API_NAME = "Trade Talk API";
    private static final String API_VERSION = "1.0";
    private static final String API_DESCRIPTION = "Trade Talk 서버 API 문서";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Arrays.asList(securityContext())) // swagger에서 jwt 토큰값 넣기위한 설정
                .securitySchemes(Arrays.asList(apiKey(), apiKey2()))// swagger에서 jwt 토큰값 넣기위한 설정
                .apiInfo(apiInfo());
    }
    private ApiKey apiKey() {
        return new ApiKey("accessToken", "accessToken", "header");
    }
    private ApiKey apiKey2() {
        return new ApiKey("refreshToken", "refreshToken", "header");
    }
    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope authorizationScope1 = new AuthorizationScope("global", "refreshEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        AuthorizationScope[] authorizationScopes1 = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        authorizationScopes1[0] = authorizationScope1;
        return Arrays.asList(new SecurityReference("accessToken", authorizationScopes), new SecurityReference("refreshToken", authorizationScopes1));
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("제목")
                .description("설명")
                .version(version)
                .contact(new Contact("이름", "홈페이지 URL", "e-mail"))
                .build();
    }


}