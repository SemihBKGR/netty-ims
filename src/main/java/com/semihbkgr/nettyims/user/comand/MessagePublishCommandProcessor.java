package com.semihbkgr.nettyims.user.comand;

import com.semihbkgr.nettyims.message.MessageHandler;
import lombok.NonNull;

public abstract class MessagePublishCommandProcessor extends PrefixCommandProcessor{

    private final String sender;
    private final MessageHandler messageHandler;

    public MessagePublishCommandProcessor(@NonNull String commandPrefix,@NonNull String sender,@NonNull MessageHandler messageHandler) {
        super(commandPrefix);
        this.sender = sender;
        this.messageHandler = messageHandler;
    }

    @Override
    final void processCommand(@NonNull String command) {
        messageHandler.broadcastMessage(sender,publish(command));
    }

    protected abstract String publish(@NonNull String command);

}
