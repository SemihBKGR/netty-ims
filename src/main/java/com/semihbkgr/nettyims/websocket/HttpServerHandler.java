package com.semihbkgr.nettyims.websocket;

import com.semihbkgr.nettyims.user.UsernameGenerator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    public static final String USERNAME_CHANNEL_ATTR = "netty-ims-username";

    private final UsernameGenerator usernameGenerator;
    private final WebSocketHandler webSocketHandler;

    @Inject
    public HttpServerHandler(@NonNull UsernameGenerator usernameGenerator, @NonNull WebSocketHandler webSocketHandler) {
        this.usernameGenerator = usernameGenerator;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest httpRequest) {
            HttpHeaders headers = httpRequest.headers();
            if (/*containsHeader(headers, HttpHeaderNames.CONNECTION, "upgrade") &&*/
                    containsHeader(headers, HttpHeaderNames.UPGRADE, "websocket")) {
                ctx.channel().attr(AttributeKey.valueOf(USERNAME_CHANNEL_ATTR)).set(usernameGenerator.username());
                ctx.pipeline().replace(this, "websocketHandler", webSocketHandler);
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