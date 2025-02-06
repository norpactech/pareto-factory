package com.norpactech.fw.factory.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${cognito.region}")
  private String cognitoRegion;

  @Value("${cognito.pool-id}")
  private String userPoolId;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
    .authorizeHttpRequests(authorizeRequests -> authorizeRequests
        .requestMatchers("/api/health").permitAll()
        .requestMatchers("/api/sales/qr-code").permitAll()
        .anyRequest().authenticated())
    .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
        .jwt(jwt -> jwt.decoder(jwtDecoder())
            .jwtAuthenticationConverter(new JwtAuthenticationConverter())))
    .cors(withDefaults()).csrf(csrf -> csrf.disable());
    return http.build();
  }

  private Customizer<CorsConfigurer<HttpSecurity>> withDefaults() {
    return cors -> cors.configurationSource(corsConfigurationSource());
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("https://dev.customer.canology.cleaning");
    configuration.addAllowedOrigin("https://customer.canology.cleaning");
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedHeader("*");
    configuration.addAllowedMethod("*");
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }  

  @Bean
  JwtDecoder jwtDecoder() {
    String jwkSetUri = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json", cognitoRegion, userPoolId);
    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
  }
}