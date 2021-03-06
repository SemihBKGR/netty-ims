package com.semihbkgr.nettyims.user.comand;

import lombok.NonNull;

import javax.inject.Inject;
import java.util.Set;

public class CommandProcessorMultiplexer implements CommandProcessor{

    private final Set<CommandProcessor> commandProcessorsSet;

    @Inject
    public CommandProcessorMultiplexer(Set<CommandProcessor> commandProcessors) {
        this.commandProcessorsSet = commandProcessors;
    }

    @Override
    public boolean isCommand(@NonNull String msg) {
        return commandProcessorsSet.stream()
                .anyMatch(commandProcessor -> commandProcessor.isCommand(msg));
    }

    @Override
    public void process(@NonNull String msg) {
        commandProcessorsSet.forEach(processor -> processor.process(msg));
    }

}
