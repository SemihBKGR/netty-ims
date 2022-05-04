package com.semihbkgr.nettyims.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ServerStatusServiceImpl implements ServerStatusService{

    private final WebClient webClient;

    public Mono<ServerStatusService.ServerStatusResponse> getStatus() {
        return webClient.get()
                .uri(ServerStatusService.SERVER_STATUS_PATH)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(ServerStatusResponse.class));
    }

}
