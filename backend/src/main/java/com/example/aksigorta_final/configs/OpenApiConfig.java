package com.example.aksigorta_final.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Aksigorta Final API")
                .version("v1")
                .description("Backend REST API documentation"));
    }

    @Bean
    public GroupedOpenApi userApiGroup() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi eventApiGroup() {
        return GroupedOpenApi.builder()
                .group("event")
                .pathsToMatch("/event/**")
                .build();
    }
}

