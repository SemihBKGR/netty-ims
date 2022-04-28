package com.semihbkgr.nettyims.kafka;

import com.semihbkgr.nettyims.message.Message;

public interface MessageBroadcaster {

    void broadcast(BroadcastData broadcastData);

}
