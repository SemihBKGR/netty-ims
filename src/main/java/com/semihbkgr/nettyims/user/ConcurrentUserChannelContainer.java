package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentUserChannelContainer implements UserChannelContainer {

    private final ConcurrentHashMap<String, Channel> usernameChannelMap;

    public ConcurrentUserChannelContainer() {
        this.usernameChannelMap=new ConcurrentHashMap<>();
    }

    @Override
    public boolean add(String username, Channel channel) {
        return usernameChannelMap.put(username,channel)==null;
    }

    @Override
    public boolean remove(String username) {
        return usernameChannelMap.remove(username)!=null;
    }

}
