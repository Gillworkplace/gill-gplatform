package com.gill.web.config;

import com.gill.web.domain.DocProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * SwaggerConfig
 *
 * @author gill
 * @version 2024/01/22
 **/
@Import(DocProperties.class)
@Configuration
public class DocConfig {

    @Bean
    public GroupedOpenApi publicApi(DocProperties docProp) {
        return GroupedOpenApi.builder()
            .group("gill-public-api")
            .pathsToMatch(docProp.getApiPathsToMatch())
            .build();
    }

    @Bean
    public OpenAPI createOpenApi(DocProperties docProp) {
        Contact contact = new Contact().name(docProp.getContactName())
            .url(docProp.getContactUrl())
            .email(docProp.getContactEmail());
        Info apiInfo = new Info().title(docProp.getTitle())
            .description(docProp.getDescription())
            .version(docProp.getVersion())
            .contact(contact);
        OpenAPI api = new OpenAPI().info(apiInfo);
//        if (docProp.isEnableSecurity()) {
//            Components components = new Components().addSecuritySchemes("bearer-jwt",
//                securityScheme());
//            SecurityRequirement securityRequirement = new SecurityRequirement()
//                .addList("bearer-jwt", Arrays.asList("read", "write"));
//            api.components(components).addSecurityItem(securityRequirement);
//        }
        return api;
    }

    private SecurityScheme securityScheme() {

        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");
    }
}
