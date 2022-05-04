package com.semihbkgr.nettyims.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ServerStatusServiceImpl implements ServerStatusService {

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public Mono<ServerStatusService.ServerStatusResponse> getStatus() {
        return webClient.get()
                .uri(ServerStatusService.SERVER_STATUS_PATH)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .flatMap(responseBody -> {
                    try {
                        return Mono.just(objectMapper.readValue(responseBody, ServerStatusResponse.class))
                                .doOnNext(serverStatusResponse -> {
                                    serverStatusResponse.setAddress(serverStatusResponse.getAddress().substring(1));
                                });
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });

    }

}
