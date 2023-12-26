package com.example.basicpjt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // 특정 주소만 JWT 적용되게 설정

    @Bean
    public OpenAPI openApi() {
        Info info = new Info()
                .title("SpringBoot3 Basic Project Swagger")
                .description("스프링부트 3버전에서 Swagger 연결 및 JWT, Security, CRUD 구현")
                .version("1.0.0");

        String jwtSchemeName = "Authorization";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components().addSecuritySchemes(jwtSchemeName,
                new SecurityScheme()
                        .name(jwtSchemeName)
                        .scheme("Bearer Token")
                        .in(SecurityScheme.In.HEADER)
                        .type(SecurityScheme.Type.APIKEY));

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
