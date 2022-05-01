package com.semihbkgr.nettyims.message;

import java.util.function.BiConsumer;

public interface MessageHandler {

    void broadcastMessage(String receiver, Message message);

    void addOnReceiveMessageListener(BiConsumer<? super String, ? super Message> listener);

}
