package com.semihbkgr.nettyims.user.comand;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PrefixCommandProcessor implements CommandProcessor {

    private final String commandPrefix;

    @Override
    public final boolean isCommand(@NonNull String msg) {
        return msg.startsWith(commandPrefix);
    }

    @Override
    public final void process(@NonNull String msg) {
        processCommand(msg.substring(commandPrefix.length()));
    }

    abstract void processCommand(@NonNull String command);

}
