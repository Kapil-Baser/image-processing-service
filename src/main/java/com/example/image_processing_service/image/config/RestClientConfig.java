package com.example.image_processing_service.image.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${supabase.s3.endpoint}")
    private String url;

    @Value("${supabase.s3.service-role-key}")
    private String key;

    @Bean
    public RestClient storageClient() {
        return RestClient.builder()
                .baseUrl(url)
                .defaultHeader("Authorization", "Bearer " + key)
                .defaultHeader("apikey", key)
                .build();

    }
}
