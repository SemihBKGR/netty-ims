package com.semihbkgr.nettyims.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public WebClient webClient(@Value("${netty-ims.gateway.hostname}") String gatewayUrl,
                               @Value("${netty-ims.gateway.port}") int gatewayPort) {
        return WebClient.builder()
                .baseUrl(String.format("http://%s:%d",gatewayUrl,gatewayPort))
                .build();
    }

}
