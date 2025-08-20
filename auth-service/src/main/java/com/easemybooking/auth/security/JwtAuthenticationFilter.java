package com.easemybooking.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Public endpoints – adjust if you have more
        return matcher.match("/api/v1/auth/register", path)
                || matcher.match("/api/v1/auth/login", path)
                || matcher.match("/actuator/**", path)
                || matcher.match("/v3/api-docs/**", path)
                || matcher.match("/swagger-ui/**", path)
                || matcher.match("/swagger-ui.html", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                var claims = jwtService.parse(token);
                var userId = claims.getSubject();

                // roles comes back as a List<?> when using jjwt-jackson
                Object rolesObj = claims.get("roles");
                var authorities = java.util.Collections.<SimpleGrantedAuthority>emptyList();

                if (rolesObj instanceof Collection<?> roles) {
                    authorities = roles.stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + String.valueOf(r)))
                            .toList();
                }

                var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {
                // Optionally log token parsing/validation errors (avoid leaking token)
            }
        }
        chain.doFilter(request, response);
    }
}
