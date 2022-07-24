package com.semihbkgr.nettyims.user.comand.instance;

import com.semihbkgr.nettyims.message.MessageHandler;
import com.semihbkgr.nettyims.user.comand.MessagePublishCommandProcessor;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class UsernameCommandProcessor extends MessagePublishCommandProcessor {

    @Inject
    public UsernameCommandProcessor(@Named("usernameCommandPrefix") @NonNull String commandPrefix,
                                    @Named("usernameCommandSender") @NonNull String sender,
                                    @NonNull MessageHandler messageHandler) {
        super(commandPrefix, sender, messageHandler);
    }

    @Override
    protected @NonNull String publish(@NonNull String username, @NonNull String command) {
        // TODO: 24.07.2022 implement
        return "username - not implemented yet";
    }

    @Override
    protected @NonNull List<String> toList(@NonNull String username) {
        return List.of(username);
    }

}
