package com.semihbkgr.nettyims.user;

import io.netty.channel.Channel;

public abstract class AbstractUserNettyNodeInstanceService implements UserNettyNodeInstanceService {

    private final String nodeId;

    protected AbstractUserNettyNodeInstanceService(String nodeId) {
        this.nodeId = nodeId;
    }

    protected final String nodeId(){
        return nodeId;
    }

}
