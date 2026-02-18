package com.example.BacK.presentation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SMS2I API BacK",
                version = "v0",
                description = "Documentation des endpoints de l'application SMS2I"
        )
)
public class OpenAPIConfig {
}