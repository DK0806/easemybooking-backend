package com.easemybooking.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@Configuration
public class UserHeadersFilter {

    @Bean
    public GlobalFilter userHeaderForwarder() {
        return (exchange, chain) ->
                exchange.getPrincipal()
                        .flatMap(principal -> {
                            if (principal instanceof JwtAuthenticationToken jwtAuth) {
                                var jwt = jwtAuth.getToken();
                                var userId = jwt.getSubject();
                                var roles = jwt.getClaimAsStringList("roles");
                                var mutated = exchange.mutate().request(r -> r.headers(h -> {
                                    h.set("X-User-Id", userId);
                                    if (roles != null && !roles.isEmpty()) {
                                        h.set("X-Roles", String.join(",", roles));
                                    }
                                })).build();
                                return chain.filter(mutated);
                            }
                            return chain.filter(exchange);
                        })
                        .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
    }
}
