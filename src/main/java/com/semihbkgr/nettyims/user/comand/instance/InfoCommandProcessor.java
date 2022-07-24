package com.semihbkgr.nettyims.user.comand.instance;

import com.semihbkgr.nettyims.message.MessageHandler;
import com.semihbkgr.nettyims.user.comand.MessagePublishCommandProcessor;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class InfoCommandProcessor extends MessagePublishCommandProcessor {

    @Inject
    public InfoCommandProcessor(@Named("infoCommandPrefix") @NonNull String commandPrefix,
                                @Named("infoCommandSender") @NonNull String sender,
                                @NonNull MessageHandler messageHandler) {
        super(commandPrefix, sender, messageHandler);
    }

    @Override
    protected @NonNull String publish(@NonNull String username, @NonNull String command) {
        return "username: "+username;
    }

    @Override
    protected @NonNull List<String> toList(@NonNull String username) {
        return List.of(username);
    }

}
