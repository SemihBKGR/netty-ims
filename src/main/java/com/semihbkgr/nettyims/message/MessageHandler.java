package com.semihbkgr.nettyims.message;

public interface MessageHandler {

    void onUserConnected(String username);

    void onUserDisconnected(String username);

    void onSend(String message);

    void onReceived(String message);

}
