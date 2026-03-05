package com.briancheruiyot.jobportal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathsConfig {

    @Bean(name = "publicPaths")
    List<String> publicPaths() {
        return List.of(
                "/register/public",
                "/api/companies/public",
                "/api/auth/login/public",
                "/api/auth/register/public",
                "/api/csrf-token/public",
                "/api/logging/public",
                "/api/contacts/public",
                "/api/swagger-ui.html",
                "/swagger-ui/**",
                "/api/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**");
    }

    @Bean(name = "securedPaths")
    List<String> securedPaths() {
        return List.of(
                "/api/**");
    }

}