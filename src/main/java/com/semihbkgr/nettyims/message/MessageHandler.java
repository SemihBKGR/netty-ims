package com.semihbkgr.nettyims.message;

import java.util.List;
import java.util.function.BiConsumer;

public interface MessageHandler {

    void broadcastMessage(Message message);

    void broadcastMessage(String from, String messageStr);

    void addOnReceiveMessageListener(BiConsumer<List<String>, Message> listener);

}
