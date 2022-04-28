package com.semihbkgr.nettyims.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.semihbkgr.nettyims.NettyIMSApp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

@Slf4j
public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private final String username;
    public WebSocketHandler(ChannelHandlerContext ctx, String username) {
        this.username = username;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        DefaultUserWSChannelContainer.getInstance().register(username, ctx.channel());
        NettyIMSApp.messageHandler.onUserConnected(username);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        DefaultUserWSChannelContainer.getInstance().unregister(username);
        NettyIMSApp.messageHandler.onUserDisconnected(username);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws JsonProcessingException, InterruptedException, KeeperException {
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof TextWebSocketFrame textWSFrame) {
                NettyIMSApp.messageHandler.onSend(username, textWSFrame.text());
            }
        }
    }

}