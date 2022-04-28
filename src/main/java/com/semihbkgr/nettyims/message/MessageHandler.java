package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.zookeeper.KeeperException;

public interface MessageHandler {

    void onUserConnected(String username);

    void onUserDisconnected(String username);

    void onSend(String username,String rawMessage) throws JsonProcessingException, InterruptedException, KeeperException;

    void onReceived(String username,String rawMessage);

}
