package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;

public interface UserActionHandler {

    void onConnect(String username, Channel channel);

    void onDisconnect(String username);

    void onMessageSend(String username, String messageStr);

    void onMessageReceive(String username, String messageStr);

}
