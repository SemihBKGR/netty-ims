package com.semihbkgr.nettyims.user.comand;

import com.semihbkgr.nettyims.message.Message;
import com.semihbkgr.nettyims.message.MessageHandler;
import lombok.NonNull;

import javax.swing.plaf.ListUI;
import java.util.List;

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
    final void processCommand(@NonNull String username,@NonNull String command) {
        var content = publish(username,command);
        var message = new Message();
        message.setContent(content);
        message.setFrom(sender);
        message.setToList(toList(username));
        messageHandler.broadcastMessage(message);
    }

    @NonNull
    protected abstract String publish(@NonNull String username, @NonNull String command);

    @NonNull
    protected abstract List<String> toList(@NonNull String username);

}
