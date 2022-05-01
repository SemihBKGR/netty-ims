package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;

public interface UserNettyNodeInstanceService {

    boolean addUser(String username);

    boolean removeUser(String username);

}
