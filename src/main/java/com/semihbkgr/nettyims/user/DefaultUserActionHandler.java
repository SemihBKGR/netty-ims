package com.semihbkgr.nettyims.user;

import com.semihbkgr.nettyims.message.MessageHandler;
import io.netty.channel.Channel;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

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
        userChannelContainer.add(username, channel);
        userNettyNodeInstanceService.addUser(username);
    }

    @Override
    public void onDisconnect(@NonNull String username) {
        userChannelContainer.remove(username);
        userNettyNodeInstanceService.removeUser(username);
    }

    @Override
    public void onMessageSend(String username, String messageStr) {
        messageHandler.broadcastMessage(username, messageStr);
    }

}
