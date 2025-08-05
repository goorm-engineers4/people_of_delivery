package com.example.cloudfour.peopleofdelivery.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Configuration
public class RestClientConfig {

    private static final String AUTHORIZATION_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BASIC_AUTH_PREFIX = "Basic ";
    private static final String EMPTY_SECRET_KEY_SUFFIX = ":";

    @Value("${toss.secret-key}")
    private String secretKey;

    @Bean
    public RestClient tossRestClient(@Value("${toss.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(AUTHORIZATION_HEADER,
                        BASIC_AUTH_PREFIX + Base64
                                .getEncoder()
                                .encodeToString((secretKey + EMPTY_SECRET_KEY_SUFFIX).getBytes()))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
