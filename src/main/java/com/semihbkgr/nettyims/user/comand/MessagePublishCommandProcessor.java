package com.semihbkgr.nettyims.user.comand;

import com.semihbkgr.nettyims.message.Message;
import com.semihbkgr.nettyims.message.MessageHandler;
import lombok.NonNull;

public abstract class MessagePublishCommandProcessor extends PrefixCommandProcessor {

    private final String sender;
    private final MessageHandler messageHandler;

    public MessagePublishCommandProcessor(@NonNull String commandPrefix,
                                          @NonNull String sender,
                                          @NonNull MessageHandler messageHandler) {
        super(commandPrefix);
        this.sender = sender;
        this.messageHandler = messageHandler;
    }

    @Override
    final void processCommand(@NonNull String command) {
        var content = publish(command);
        var message = new Message();
        message.setContent(content);
        message.setFrom(sender);
        messageHandler.broadcastMessage(message);
    }

    protected abstract String publish(@NonNull String command);

}
