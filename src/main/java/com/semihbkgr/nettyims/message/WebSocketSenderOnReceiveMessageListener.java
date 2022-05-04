package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihbkgr.nettyims.user.UserChannelContainer;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

@Slf4j
@Singleton
public class WebSocketSenderOnReceiveMessageListener implements BiConsumer<List<String>, Message> {

    private final UserChannelContainer userChannelContainer;
    private final ObjectMapper objectMapper;

    @Inject
    public WebSocketSenderOnReceiveMessageListener(@NonNull UserChannelContainer userChannelContainer,
                                                   @NonNull ObjectMapper objectMapper) {
        this.userChannelContainer = userChannelContainer;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public void accept(@NonNull List<String> receiverUsernames, @NonNull Message message) {
        var serializedMessage = objectMapper.writeValueAsString(message);
        if (receiverUsernames.isEmpty()) {
            log.info("message is sending all users, usersCount: {}, message: {}", userChannelContainer.size(), message);
            userChannelContainer.all()
                    .parallelStream()
                    .forEach(c -> c.writeAndFlush(new TextWebSocketFrame(serializedMessage)));
        } else {
            receiverUsernames.parallelStream()
                    .map(username -> {
                        var channel = userChannelContainer.get(username);
                        if (channel == null) {
                            log.warn("username: {} channel is null", username);
                        } else {
                            log.info("message is sending, username: {}, message: {}", username, message);
                        }
                        return channel;
                    })
                    .filter(Objects::nonNull)
                    .forEach(c -> c.writeAndFlush(new TextWebSocketFrame(serializedMessage)));
        }
    }

}
