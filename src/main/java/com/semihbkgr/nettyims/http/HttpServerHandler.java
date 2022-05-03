package com.semihbkgr.nettyims.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihbkgr.nettyims.http.response.ErrorResponse;
import com.semihbkgr.nettyims.http.response.ServerStatusResponse;
import com.semihbkgr.nettyims.user.UserChannelContainer;
import com.semihbkgr.nettyims.user.UsernameGenerator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
@Singleton
@ChannelHandler.Sharable
public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    public static final String USERNAME_CHANNEL_ATTR = "netty-ims-username";
    public static final String SERVER_STATUS_URL = "/status";
    public static final String CHAT_WS_HANDSHAKE_URL = "/chat";

    private final String serverNodeId;
    private final UsernameGenerator usernameGenerator;
    private final UserChannelContainer userChannelContainer;
    private final ChatWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    @Inject
    public HttpServerHandler(@NonNull @Named("serverNodeId") String serverNodeId,
                             @NonNull UsernameGenerator usernameGenerator,
                             @NonNull UserChannelContainer userChannelContainer,
                             @NonNull ChatWebSocketHandler chatWebSocketHandler,
                             @NonNull ObjectMapper objectMapper) {
        this.serverNodeId = serverNodeId;
        this.usernameGenerator = usernameGenerator;
        this.userChannelContainer = userChannelContainer;
        this.webSocketHandler = chatWebSocketHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Incoming request - type: {}",msg.getClass().getName());
        if (msg instanceof HttpRequest httpRequest) {
            if (httpRequest.uri().equals(SERVER_STATUS_URL)) {
                var serverStatusResponse = new ServerStatusResponse();
                serverStatusResponse.setNodeId(serverNodeId);
                serverStatusResponse.setUsersCount(userChannelContainer.size());
                serverStatusResponse.setAddress(ctx.channel().localAddress().toString());
                var serializedServerStatusResponseBytes = objectMapper.writeValueAsBytes(serverStatusResponse);
                var byteBuf = Unpooled.wrappedBuffer(serializedServerStatusResponseBytes);
                ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf));
            } else if (httpRequest.uri().equals(CHAT_WS_HANDSHAKE_URL)) {
                HttpHeaders headers = httpRequest.headers();
                // TODO: 5/3/22 HttpHeaderNames.CONNECTION: upgrade
                if ( /*containsHeader(headers, HttpHeaderNames.CONNECTION, "upgrade") &&*/
                        containsHeader(headers, HttpHeaderNames.UPGRADE, "websocket")) {
                    ctx.channel().attr(AttributeKey.valueOf(USERNAME_CHANNEL_ATTR)).set(usernameGenerator.username());
                    ctx.pipeline().replace(this, "websocketHandler", webSocketHandler);
                    handleHandshake(ctx, httpRequest);
                    ctx.fireChannelRegistered();
                } else {
                    var errorResponse = new ErrorResponse();
                    errorResponse.setTimestamp(System.currentTimeMillis());
                    errorResponse.setUrl(httpRequest.uri());
                    errorResponse.setMessage("unsatisfied websocket handshake request");
                    errorResponse.setStatus(HttpResponseStatus.BAD_REQUEST.code());
                    var serializedErrorResponseBytes = objectMapper.writeValueAsBytes(errorResponse);
                    var byteBuf = Unpooled.wrappedBuffer(serializedErrorResponseBytes);
                    ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, byteBuf));
                }
            } else {
                var errorResponse = new ErrorResponse();
                errorResponse.setTimestamp(System.currentTimeMillis());
                errorResponse.setUrl(httpRequest.uri());
                errorResponse.setMessage("unavailable request url");
                errorResponse.setStatus(HttpResponseStatus.BAD_REQUEST.code());
                var serializedErrorResponseBytes = objectMapper.writeValueAsBytes(errorResponse);
                var byteBuf = Unpooled.wrappedBuffer(serializedErrorResponseBytes);
                ctx.channel().writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST, byteBuf));
            }
        } else {
            log.info("Incoming request type is not supported");
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