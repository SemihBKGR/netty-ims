package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentUserChannelContainer implements UserChannelContainer {

    private final ConcurrentHashMap<String, Channel> usernameChannelMap;

    public ConcurrentUserChannelContainer() {
        this.usernameChannelMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean add(@NonNull String username, @NonNull Channel channel) {
        return usernameChannelMap.put(username, channel) == null;
    }

    @Override
    public Channel get(@NonNull String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public boolean remove(@NonNull String username) {
        return usernameChannelMap.remove(username) != null;
    }

}
