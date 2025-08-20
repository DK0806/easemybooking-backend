package com.easemybooking.booking.security;

//import com.ey.springboot3security.service.UserInfoDetails;
//import com.ey.springboot3security.service.JwtService;

import com.easemybooking.booking.model.UserInfo;
import com.easemybooking.booking.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

//    @Autowired
//    JwtProperties jwtProperties;

    //private final UserDetailsService userDetailsService;

    private final JwtService jwtService;

    //private final JwtService jwtService;


    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    //@Autowired
    public JwtAuthFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        //this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        //JwtProperties jwtProperties = new JwtProperties(jwtProperties);

        //private final JwtService jwtService;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            Claims claims =jwtService.parse(token);
            username = claims.getSubject();
            List<String> roles = (List<String>) claims.get("roles");
            List<SimpleGrantedAuthority> authorities =
                    roles == null
                            ? List.of()
                            : roles.stream()
                            .filter(java.util.Objects::nonNull)
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r) // ensure ROLE_ prefix for hasRole(...)
                            .map(SimpleGrantedAuthority::new)
                            .toList();
            UserInfo userInfo = new UserInfo();
            userInfo.setId(username);
            userInfo.setPassword("12345678");
            userInfo.setRoles(authorities);

            UserInfoDetails userDetails = new UserInfoDetails(userInfo);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

           // username = jwtService.extractUsername(token);
        }

//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserInfoDetails userDetails = new UserInfoDetails();
////            if (jwtService.validateToken(token, userDetails)) {
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails,
//                        null,
//                        userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            //}
//        }
        filterChain.doFilter(request, response);
    }
}