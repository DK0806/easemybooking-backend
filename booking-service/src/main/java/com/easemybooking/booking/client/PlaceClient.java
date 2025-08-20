package com.easemybooking.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Component
public class PlaceClient {
    private final WebClient webClient;
    private final String placePath;
    private final Duration timeout;

    public record PlaceDto(UUID placeId, String name, String city, Integer capacity, BigDecimal pricePerSlot) {}

    public PlaceClient(@Value("${place.base-url}") String baseUrl,
                       @Value("${place.place-path}") String placePath,
                       @Value("${place.timeout-ms:2000}") long timeoutMs
                       // If you want to forward auth to place-service later:
                       // @Value("${place.forward-bearer:false}") boolean forwardBearer
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                // If you want to always forward a token, add an ExchangeFilterFunction here.
                .build();
        this.placePath = placePath;     // e.g. "/api/v1/places/{id}"
        this.timeout = Duration.ofMillis(timeoutMs);
    }

    public PlaceDto getPlace(UUID placeId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(placePath).build(placeId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new RuntimeException("Place not found: " + placeId)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("Place service temporary error")))
                .bodyToMono(PlaceDto.class)
                .block(timeout);
    }
}
