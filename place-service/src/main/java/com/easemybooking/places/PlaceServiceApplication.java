package com.easemybooking.places;

import com.easemybooking.places.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PlaceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlaceServiceApplication.class, args);
    }
}
