package com.semihbkgr.nettyims.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.semihbkgr.nettyims.msg.DefaultMessageHandler;
import com.semihbkgr.nettyims.msg.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private static final MessageHandler messageHandler = new DefaultMessageHandler();

    private final String username;
    public WebSocketHandler(ChannelHandlerContext ctx, String username) {
        this.username = username;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        DefaultUserWSChannelContainer.getInstance().register(username, ctx.channel());
        messageHandler.onUserConnected(username);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        DefaultUserWSChannelContainer.getInstance().unregister(username);
        messageHandler.onUserDisconnected(username);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws JsonProcessingException {
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof TextWebSocketFrame textWSFrame) {
                messageHandler.onSend(textWSFrame.text());
            }
        }
    }

}