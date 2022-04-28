package com.semihbkgr.nettyims.msg;

public interface MessageHandler {

    void onUserConnected(String username);

    void onUserDisconnected(String username);

    void onSend(String message);

    void onReceived(String message);

}
