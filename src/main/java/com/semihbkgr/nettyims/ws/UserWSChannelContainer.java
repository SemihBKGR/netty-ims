package com.semihbkgr.nettyims.ws;

import io.netty.channel.Channel;

public interface UserWSChannelContainer {

    void register(String username, Channel channel);

    void unregister(String username);

    Channel getChannel(String username);

}
