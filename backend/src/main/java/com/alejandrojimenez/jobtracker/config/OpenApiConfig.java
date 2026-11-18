package com.alejandrojimenez.jobtracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Title and description shown at the top of Swagger UI. */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI jobTrackerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("JobTracker API")
                .version("1.0")
                .description("REST API to save job offers and follow each application from saved to offer.")
                .contact(new Contact()
                        .name("Alejandro Jiménez Pinzón")
                        .url("https://alejandro-jimenez-portafolio.netlify.app/")));
    }
}
