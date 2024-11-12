package com.mb.microservices.product.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class OpenAPIConfig {

    @Bean
    OpenAPI productServiceAPI() {
        return new OpenAPI().info(new Info().title("Product Service API"));
    }
}

