package com.movie.celestix.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("celestix-public")
                .pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(dynamicTagNames())
                .build();
    }

    @Bean
    public OpenApiCustomizer dynamicTagNames() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem ->
                        pathItem.readOperations().forEach(operation -> {
                            if (operation.getTags() != null) {
                                operation.setTags(
                                        operation.getTags().stream()
                                                .map(this::toApiName)
                                                .collect(Collectors.toList())
                                );
                            }
                            operation.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
                        })
                );
            }
        };
    }

    private String toApiName(String original) {
        String nameWithoutSuffix = original.replaceAll("-controller$", "");
        String[] parts = nameWithoutSuffix.split("-");
        String capitalized = java.util.Arrays.stream(parts)
                .map(p -> p.substring(0, 1).toUpperCase() + p.substring(1))
                .collect(Collectors.joining(" "));
        return capitalized + " API";
    }

    @Bean
    public OpenAPI lmsOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Celestix API")
                        .description("Theater Management System API documentation")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token in the format: Bearer <token>")
                        )
                );
    }
}
