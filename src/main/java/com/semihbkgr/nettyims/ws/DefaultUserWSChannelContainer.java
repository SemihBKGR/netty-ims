package com.semihbkgr.nettyims.ws;

import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultUserWSChannelContainer implements UserWSChannelContainer {

    private final ConcurrentHashMap<String, Channel> usernameChannelMap;

    public DefaultUserWSChannelContainer() {
        this.usernameChannelMap = new ConcurrentHashMap<>();
    }

    @Override
    public void register(@NonNull String username, @NonNull Channel channel) {
        usernameChannelMap.put(username, channel);
    }

    @Override
    public void unregister(@NonNull String username) {
        usernameChannelMap.remove(username);
    }

    @NonNull
    @Override
    public Channel getChannel(@NonNull String username) {
        return usernameChannelMap.get(username);
    }

}