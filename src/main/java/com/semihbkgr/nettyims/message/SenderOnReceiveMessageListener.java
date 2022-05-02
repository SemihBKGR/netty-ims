package com.semihbkgr.nettyims.message;

import com.semihbkgr.nettyims.user.UserChannelContainer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

@Slf4j
@Singleton
public class SenderOnReceiveMessageListener implements BiConsumer<List<String>, Message> {

    private final UserChannelContainer userChannelContainer;

    @Inject
    public SenderOnReceiveMessageListener(@NonNull UserChannelContainer userChannelContainer) {
        this.userChannelContainer = userChannelContainer;
    }

    @Override
    public void accept(@NonNull List<String> receiverUsernames, @NonNull Message message) {
        if (receiverUsernames.isEmpty()) {
            log.info("message is sending all users, usersCount: {}, message: {}", userChannelContainer.size(), message);
            userChannelContainer.all()
                    .forEachRemaining(c -> c.writeAndFlush(message));
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
                    .forEach(c -> c.writeAndFlush(message));
        }
    }

}
