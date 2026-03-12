package com.briancheruiyot.jobportal.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.briancheruiyot.jobportal.security.filter.JwtTokenValidatorFilter;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JobPortalSecurityConfig {

    @Qualifier("publicPaths")
    private final List<String> publicPaths;

    @Qualifier("securedPaths")
    private final List<String> securedPaths;

    @Qualifier("adminPaths")
    private final List<String> adminPaths;

    @Qualifier("employerPaths")
    private final List<String> employerPaths;

    @Qualifier("jobseekerPaths")
    private final List<String> jobseekerPaths;

    @Bean
    SecurityFilterChain customSecurityFilterChain(HttpSecurity http) {
        return http
                .csrf(csrfConfig -> csrfConfig.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(requests -> {
                    publicPaths.forEach(path -> requests.requestMatchers(path).permitAll());
                    jobseekerPaths.forEach(path -> requests.requestMatchers(path).hasRole("JOB_SEEKER"));
                    adminPaths.forEach(path -> requests.requestMatchers(path).hasRole("ADMIN"));
                    employerPaths.forEach(path -> requests.requestMatchers(path).hasRole("EMPLOYER"));
                    securedPaths.forEach(path -> requests.requestMatchers(path).authenticated());
                    requests.anyRequest().denyAll();
                })
                // .csrf(csrfConfig -> csrfConfig.disable())
                // requests.requestMatchers("/api/companies/public").permitAll()
                // .requestMatchers("/api/contacts/public").permitAll())
                // requests.requestMatchers(RegexRequestMatcher.regexMatcher(".*public$")).permitAll()
                // .requestMatchers("/api/swagger-ui.html",
                // "/swagger-ui/**",
                // "/api/v3/api-docs/**",
                // "/swagger-resources/**",
                // "/swagger-ui.html",
                // "/webjars/**").permitAll())
                .addFilterBefore(new JwtTokenValidatorFilter(publicPaths), BasicAuthenticationFilter.class)
                .formLogin(flc -> flc.disable())
                .httpBasic(hbc -> hbc.disable())
                // .httpBasic(withDefaults())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\": \"Access Denied\", \"message\": \"You don't have permission to access this resource\"}");
                        })
                // .authenticationEntryPoint((request, response, authException) -> {
                // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // response.setContentType("application/json");
                // response.getWriter().write("{\"error\": \"Unauthorized\", \"message\":
                // \"Authentication required\"}");
                // })

                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // @Bean
    // UserDetailsService userDetailsService() {
    // System.out.println(passwordEncoder().encode("brian_7899"));
    // var user1 = User.builder().username("brian")
    // .password("$2a$10$OqUZcccTquF6B70SCwXQieVdW28CicB0d7nl41EMIQHPgw99ZawSG").roles("USER").build();
    // var user2 = User.builder().username("brian2")
    // .password("$2a$10$OqUZcccTquF6B70SCwXQieVdW28CicB0d7nl41EMIQHPgw99ZawSG").roles("ADMIN").build();

    // return new InMemoryUserDetailsManager(user1, user2);
    // }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        // var authenticationProvider = new
        // DaoAuthenticationProvider(userDetailsService());
        // authenticationProvider.setPasswordEncoder(passwordEncoder());
        // return new ProviderManager(authenticationProvider);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
