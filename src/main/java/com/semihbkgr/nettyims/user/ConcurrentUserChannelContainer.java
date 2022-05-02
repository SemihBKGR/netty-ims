package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ConcurrentUserChannelContainer implements UserChannelContainer {

    private final ConcurrentHashMap<String, Channel> usernameChannelMap;

    @Inject
    public ConcurrentUserChannelContainer() {
        this.usernameChannelMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(@NonNull String username, @NonNull Channel channel) {
        usernameChannelMap.put(username, channel);
    }

    @Override
    public Channel get(@NonNull String username) {
        return usernameChannelMap.get(username);
    }

    @Override
    public Iterator<Channel> all() {
        return usernameChannelMap.values().iterator();
    }

    @Override
    public void remove(@NonNull String username) {
        usernameChannelMap.remove(username);
    }

}
