package com.gill.web.config;

import com.gill.web.domain.SwaggerProperties;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfig
 *
 * @author gill
 * @version 2024/01/22
 **/
@Import(SwaggerProperties.class)
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi(SwaggerProperties swaggerProperties) {
        Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo(swaggerProperties))
            .select()
            .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getApiBasePackage()))
            .paths(PathSelectors.any())
            .build();
        if (swaggerProperties.isEnableSecurity()) {
            docket.securitySchemes(securitySchemes()).securityContexts(securityContexts());
        }
        return docket;
    }

    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        Contact contact = new Contact(swaggerProperties.getContactName(),
            swaggerProperties.getContactUrl(), swaggerProperties.getContactEmail());
        return new ApiInfoBuilder().title(swaggerProperties.getTitle())
            .description(swaggerProperties.getDescription())
            .contact(contact)
            .version(swaggerProperties.getVersion())
            .build();
    }

    private List<SecurityScheme> securitySchemes() {

        //设置请求头信息
        List<SecurityScheme> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        result.add(apiKey);
        return result;
    }

    private List<SecurityContext> securityContexts() {

        //设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextByPath("/*/.*"));
        return result;
    }

    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .operationSelector(oc -> oc.requestMappingPattern().matches(pathRegex))
            .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global",
            "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference("Authorization", authorizationScopes));
        return result;
    }
}
