package com.semihbkgr.nettyims.message;

public interface MessageHandler {

    void onBroadcastMessage(String username, Message message);

    void onReceiveMessage(String username, Message message);

}
