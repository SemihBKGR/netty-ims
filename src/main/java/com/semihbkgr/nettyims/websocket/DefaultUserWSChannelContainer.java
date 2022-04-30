package com.semihbkgr.nettyims.websocket;

import io.netty.channel.Channel;
import lombok.NonNull;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultUserWSChannelContainer implements UserWSChannelContainer {

    private static final UserWSChannelContainer instance = new DefaultUserWSChannelContainer();

    public static UserWSChannelContainer getInstance() {
        return instance;
    }

    private final ConcurrentHashMap<String, Channel> usernameChannelMap;

    private DefaultUserWSChannelContainer() {
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

    @Override
    public Iterable<Channel> getAllChannels() {
        return usernameChannelMap.values();
    }

    @Override
    public boolean existsChannel(@NonNull String username) {
        return usernameChannelMap.contains(username);
    }

}
