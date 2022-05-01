package com.semihbkgr.nettyims.user;

import lombok.NonNull;

public abstract class AbstractUserNettyNodeInstanceService implements UserNettyNodeInstanceService {

    private final String nodeId;

    protected AbstractUserNettyNodeInstanceService(@NonNull String nodeId) {
        this.nodeId = nodeId;
    }

    protected final String nodeId(){
        return nodeId;
    }

}
