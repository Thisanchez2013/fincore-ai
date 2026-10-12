package com.fincore.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fincoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FinCore AI - API Financeira")
                        .version("v1")
                        .description(
                                "API REST para gerenciamento de contas "
                                        + "e transacoes financeiras. "
                                        + "Desenvolvida com Java 21, Spring Boot "
                                        + "e PostgreSQL, com foco em boas praticas "
                                        + "de engenharia de software e futura "
                                        + "integracao com inteligencia artificial."
                        )
                        .contact(new Contact()
                                .name("FinCore AI")
                                .url("https://github.com/Thisanchez2013/fincore-ai")
                        )
                );
    }
}
