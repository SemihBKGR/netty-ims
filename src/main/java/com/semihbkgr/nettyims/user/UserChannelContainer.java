package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Iterator;

public interface UserChannelContainer {

    void add(String username, Channel channel);

    Channel get(String username);

    Collection<Channel> all();

    int size();

    void remove(String username);

}
