package com.nextmove.transaction_service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nextMoveOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Transaction Service API - NextMove")
                        .description("API responsável pela gestão de transações financeiras no NextMove.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Equipe NextMove")
                                .email("contato@nextmove.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentação completa")
                        .url("https://nextmove.com/docs"));
    }
}