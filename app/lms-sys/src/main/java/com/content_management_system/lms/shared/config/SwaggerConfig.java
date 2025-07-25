package com.content_management_system.lms.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("lms-public")
                .pathsToMatch("/lms/**")
                .build();
    }

    @Bean
    public OpenAPI lmsOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("LMS API")
                        .description("Learning Management System API documentation")
                        .version("v1.0.0"));
    }
}