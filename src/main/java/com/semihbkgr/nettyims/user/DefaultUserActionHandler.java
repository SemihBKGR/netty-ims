package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultUserActionHandler implements UserActionHandler {

    private final UserChannelContainer userChannelContainer;
    private final UserNettyNodeInstanceService userNettyNodeInstanceService;
    private final UserNettyNodeSearchService userNettyNodeSearchService;

    @Override
    public void onConnect(String username, Channel channel) {
        userChannelContainer.add(username, channel);
        userNettyNodeInstanceService.addUser(username);
    }

    @Override
    public void onDisconnect(String username) {
        userChannelContainer.remove(username);
        userNettyNodeInstanceService.removeUser(username);
    }

    @Override
    public void onMessageSend(String username, String messageStr) {

    }

    @Override
    public void onMessageReceive(String username, String messageStr) {

    }

}
