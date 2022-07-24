package com.semihbkgr.nettyims.user.comand.instance;

import com.semihbkgr.nettyims.message.MessageHandler;
import com.semihbkgr.nettyims.user.comand.MessagePublishCommandProcessor;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class HelpCommandProcessor extends MessagePublishCommandProcessor {

    @Inject
    public HelpCommandProcessor(@Named("helpCommandPrefix") @NonNull String commandPrefix,
                                @Named("helpCommandSender") @NonNull String sender,
                                @NonNull MessageHandler messageHandler) {
        super(commandPrefix, sender, messageHandler);
    }

    @Override
    protected @NonNull String publish(@NonNull String username, @NonNull String command) {
        return "/help /info /server /username [username]";
    }

    @Override
    protected @NonNull List<String> toList(@NonNull String username) {
        return List.of(username);
    }

}
