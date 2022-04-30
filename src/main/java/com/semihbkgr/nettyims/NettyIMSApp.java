package com.semihbkgr.nettyims;

import com.semihbkgr.nettyims.kafka.MessageReceiverImpl;
import com.semihbkgr.nettyims.message.DefaultMessageHandler;
import com.semihbkgr.nettyims.message.MessageHandler;
import com.semihbkgr.nettyims.websocket.HttpInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

public class NettyIMSApp {

    public static final String NODE_ID= UUID.randomUUID().toString();

    public static final MessageHandler messageHandler;

    static {
        try {
            messageHandler = new DefaultMessageHandler();
        } catch (IOException | InterruptedException | KeeperException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:29092");
        Admin admin = Admin.create(properties);
        admin.createTopics(Collections.singleton(new NewTopic(NODE_ID, 1, (short) 1)));
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        new MessageReceiverImpl(messageHandler).startReceiving(NODE_ID);

        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpInitializer());

            Channel ch = b.bind(9000).sync().channel();

            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
