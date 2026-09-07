package com.wthxrsh.grwm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public RestClient weatherRestClient() {
        return RestClient.builder().baseUrl("https://api.open-meteo.com").build();
    }

    @Bean
    public RestClient geocodingRestClient() {
        return RestClient.builder().baseUrl("https://geocoding-api.open-meteo.com").build();
    }

    @Bean
    public RestClient reverseGeocodingRestClient() {
        return RestClient.builder().baseUrl("https://api.bigdatacloud.net").build();
    }
}