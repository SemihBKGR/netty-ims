package com.semihbkgr.nettyims.websocket;

import io.netty.channel.Channel;

public interface UserWSChannelContainer {

    void register(String username, Channel channel);

    void unregister(String username);

    Channel getChannel(String username);

    boolean existsChannel(String username);

}
