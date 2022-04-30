package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.zookeeper.KeeperException;

public interface MessageHandler {

    void onUserConnected(String username) throws InterruptedException, KeeperException;

    void onUserDisconnected(String username) throws InterruptedException, KeeperException;

    void onSend(String username,String rawMessage) throws JsonProcessingException, InterruptedException, KeeperException;

    void onReceived(String username,String rawMessage);

}
