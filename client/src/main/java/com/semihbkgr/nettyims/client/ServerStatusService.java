package com.semihbkgr.nettyims.client;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface ServerStatusService {

    String SERVER_STATUS_PATH="/status";

    Mono<ServerStatusResponse> getStatus();

    @Data
    class ServerStatusResponse {

        private String nodeId;

        private String address;

        private int usersCount;

    }

}
