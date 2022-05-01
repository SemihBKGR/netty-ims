package com.semihbkgr.nettyims.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihbkgr.nettyims.message.Message;
import com.semihbkgr.nettyims.message.MessageHandler;
import io.netty.channel.Channel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultUserActionHandler implements UserActionHandler {

    private final UserChannelContainer userChannelContainer;
    private final UserNettyNodeInstanceService userNettyNodeInstanceService;
    private final UserNettyNodeSearchService userNettyNodeSearchService;
    private final MessageHandler messageHandler;
    private final ObjectMapper objectMapper;

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
        try {
            var nodeId = userNettyNodeSearchService.findNodeId(username);
            var message = objectMapper.readValue(nodeId, Message.class);
            message.setFrom(username);
            message.setTimestamp(System.currentTimeMillis());
            if (message.getToList().isEmpty()) {
                throw new IllegalStateException("message toList cannot be empty");
            }
            message.getToList().forEach(to -> messageHandler.broadcastMessage(to, message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
