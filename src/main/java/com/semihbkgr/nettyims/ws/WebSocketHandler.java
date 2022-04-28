package com.semihbkgr.nettyims.ws;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private static final UserWSChannelContainer userWSChannelContainer = new DefaultUserWSChannelContainer();

    private final String username;

    public WebSocketHandler(ChannelHandlerContext ctx, String username) {
        this.username = username;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        userWSChannelContainer.register(username, ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof WebSocketFrame) {
            if (msg instanceof TextWebSocketFrame a) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame("Message received : " + a.text()));
            }
        }
    }

}