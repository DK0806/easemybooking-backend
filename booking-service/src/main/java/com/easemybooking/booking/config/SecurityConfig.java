package com.easemybooking.booking.config;

import com.easemybooking.booking.security.JwtAuthFilter;
import com.easemybooking.booking.security.JwtAuthenticationFilter;
import com.easemybooking.booking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService);
    }


    @Value("${app.security.disabled:false}")
    private boolean securityDisabled;

    //@Value("${jwt.secret}")   // Base64 HS256 secret (same as auth-service)
    //private String jwtSecret;

    @Value("${jwt.issuer}")   // Must match auth-service issuer
    private String jwtIssuer;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (securityDisabled) {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(reg -> reg.anyRequest().permitAll())
                    .build();
        }

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        String secretKeyP = "R40kJmeSk4U5iIDiuYVencVQyYtqMeAQ7Pq3Fmq+pvgy0fyFldOhwBNxT7Wjh5thSg4TkpR6tRLs9UboXjV4TA==";
        //byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);// Base64 from application.yml / env
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyP);
        var key = new SecretKeySpec(keyBytes, "HmacSHA256");

        var decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        OAuth2TokenValidator<Jwt> validator = JwtValidators.createDefaultWithIssuer(jwtIssuer);
        decoder.setJwtValidator(validator);
        return decoder;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {
        var roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthorityPrefix("ROLE_");
        roles.setAuthoritiesClaimName("roles"); // matches auth-service claim

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
}
