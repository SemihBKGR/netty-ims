package com.semihbkgr.nettyims.user;

import com.semihbkgr.nettyims.message.MessageHandler;
import com.semihbkgr.nettyims.user.comand.CommandProcessor;
import com.semihbkgr.nettyims.user.comand.CommandProcessorMultiplexer;
import io.netty.channel.Channel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Singleton
public class AsynchronousUserActionHandler implements UserActionHandler {

    private final UserChannelContainer userChannelContainer;
    private final UserNettyNodeInstanceService userNettyNodeInstanceService;
    private final MessageHandler messageHandler;
    private final CommandProcessor commandProcessor;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Inject
    public AsynchronousUserActionHandler(@NonNull UserChannelContainer userChannelContainer,
                                         @NonNull UserNettyNodeInstanceService userNettyNodeInstanceService,
                                         @NonNull MessageHandler messageHandler,
                                         @NonNull CommandProcessorMultiplexer commandProcessorMultiplexer) {
        this.userChannelContainer = userChannelContainer;
        this.userNettyNodeInstanceService = userNettyNodeInstanceService;
        this.messageHandler = messageHandler;
        this.commandProcessor=commandProcessorMultiplexer;
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Override
    public void onConnect(@NonNull String username, @NonNull Channel channel) {
        log.info("onConnect - username: {}", username);
        userChannelContainer.add(username, channel);
        threadPoolExecutor.execute(() -> userNettyNodeInstanceService.addUser(username));
    }

    @Override
    public void onDisconnect(@NonNull String username) {
        log.info("onDisconnect - username: {}", username);
        userChannelContainer.remove(username);
        threadPoolExecutor.execute(() -> userNettyNodeInstanceService.removeUser(username));
    }

    @Override
    public void onMessageSend(String username, String messageStr) {
        log.info("onMessageSend - username: {}, message: {}", username, messageStr);
        if (commandProcessor.isCommand(messageStr)){
            threadPoolExecutor.execute(() -> commandProcessor.process(messageStr));
        }else {
            threadPoolExecutor.execute(() -> messageHandler.broadcastMessage(username, messageStr));
        }
    }

}
