package com.semihbkgr.nettyims.user;

import com.semihbkgr.nettyims.message.MessageHandler;
import io.netty.channel.Channel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class DefaultUserActionHandler implements UserActionHandler {

    private final UserChannelContainer userChannelContainer;
    private final UserNettyNodeInstanceService userNettyNodeInstanceService;
    private final MessageHandler messageHandler;

    @Inject
    public DefaultUserActionHandler(@NonNull UserChannelContainer userChannelContainer,
                                    @NonNull UserNettyNodeInstanceService userNettyNodeInstanceService,
                                    @NonNull MessageHandler messageHandler) {
        this.userChannelContainer = userChannelContainer;
        this.userNettyNodeInstanceService = userNettyNodeInstanceService;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onConnect(@NonNull String username, @NonNull Channel channel) {
        log.info("onConnect - username: {}", username);
        userChannelContainer.add(username, channel);
        userNettyNodeInstanceService.addUser(username);
    }

    @Override
    public void onDisconnect(@NonNull String username) {
        log.info("onDisconnect - username: {}", username);
        userChannelContainer.remove(username);
        userNettyNodeInstanceService.removeUser(username);
    }

    @Override
    public void onMessageSend(String username, String messageStr) {
        log.info("onMessageSend - username: {}, message: {}", username, messageStr);
        messageHandler.broadcastMessage(username, messageStr);
    }

}
