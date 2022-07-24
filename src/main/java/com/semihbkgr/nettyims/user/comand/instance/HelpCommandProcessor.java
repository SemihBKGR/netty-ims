package com.semihbkgr.nettyims.user.comand.instance;

import com.semihbkgr.nettyims.message.MessageHandler;
import com.semihbkgr.nettyims.user.comand.MessagePublishCommandProcessor;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Named;

public class HelpCommandProcessor extends MessagePublishCommandProcessor {

    @Inject
    public HelpCommandProcessor(@Named("helpCommandPrefix") @NonNull String commandPrefix,
                                @Named("helpCommandSender") @NonNull String sender,
                                @NonNull MessageHandler messageHandler) {
        super(commandPrefix, sender, messageHandler);
    }

    @Override
    protected String publish(@NonNull String command) {
        return "help";
    }

}
