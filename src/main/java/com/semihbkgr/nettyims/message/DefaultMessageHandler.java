package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultMessageHandler implements MessageHandler {

    private final ObjectMapper objectMapper;

    public DefaultMessageHandler() {
        this.objectMapper=new ObjectMapper();
    }

    @Override
    public void onUserConnected(String username) {

    }

    @Override
    public void onUserDisconnected(String username) {

    }

    @Override
    public void onSend(String message) {

    }

    @Override
    public void onReceived(String message) {

    }

}
