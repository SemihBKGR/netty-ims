package com.semihbkgr.nettyims.websocket;

import com.semihbkgr.nettyims.UsernameGenerator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest httpRequest) {
            HttpHeaders headers = httpRequest.headers();
            if (/*containsHeader(headers, HttpHeaderNames.CONNECTION, "upgrade") &&*/
                    containsHeader(headers, HttpHeaderNames.UPGRADE, "websocket")) {
                ctx.pipeline().replace(this, "websocketHandler", new WebSocketHandler(ctx,UsernameGenerator.randomUsername()));
                handleHandshake(ctx, httpRequest);
                ctx.fireChannelRegistered();
            }
        } else {
            log.info("Incoming request is unknown");
            ctx.close();
        }
    }

    private void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory =
                new WebSocketServerHandshakerFactory(getWebSocketURL(req), null, true);
        var handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private String getWebSocketURL(HttpRequest req) {
        return "ws://" + req.headers().get("Host") + req.uri();
    }

    private boolean containsHeader(HttpHeaders headers, CharSequence name, String value) {
        return headers.getAllAsString(name)
                .stream()
                .anyMatch(h -> h.equalsIgnoreCase(value));
    }

}