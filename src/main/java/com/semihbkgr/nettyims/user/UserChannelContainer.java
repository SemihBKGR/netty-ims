package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;

public interface UserChannelContainer {

    boolean add(String username, Channel channel);

    Channel get(String username);

    boolean remove(String username);

}
