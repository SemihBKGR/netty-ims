package com.semihbkgr.nettyims.websocket;

import com.semihbkgr.nettyims.user.UserActionHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@ChannelHandler.Sharable
public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private final UserActionHandler userActionHandler;

    @Inject
    public WebSocketHandler(@NonNull UserActionHandler userActionHandler) {
        this.userActionHandler = userActionHandler;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        var username = (String) ctx.channel().attr(AttributeKey.valueOf(HttpServerHandler.USERNAME_CHANNEL_ATTR)).get();
        log.info("channelRegistered - username: {}", username);
        userActionHandler.onConnect(username, ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        var username = (String) ctx.channel().attr(AttributeKey.valueOf(HttpServerHandler.USERNAME_CHANNEL_ATTR)).get();
        log.info("channelUnregistered - username: {}", username);
        userActionHandler.onDisconnect(username);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        var username = (String) ctx.channel().attr(AttributeKey.valueOf(HttpServerHandler.USERNAME_CHANNEL_ATTR)).get();
        if (msg instanceof TextWebSocketFrame textWSFrame) {
            log.info("channelRead - username: {}, message: {}", username, textWSFrame.text());
            userActionHandler.onMessageSend(username, textWSFrame.text());
        }
    }

}