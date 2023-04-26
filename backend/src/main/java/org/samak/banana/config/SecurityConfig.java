package org.samak.banana.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http
                .cors()
                .and()
                .headers()
                .frameOptions().disable()
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(WebSocketBrokerConfiguration.ENDPOINT_PREFIX).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .build();
    }

    /**
     * Apply CORS configuration before Spring Security.
     * By default, "http.cors" take a bean called corsConfigurationSource.
     *
     * @return a CORS configuration source.
     * @implNote https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#cors
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
