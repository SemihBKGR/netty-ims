package com.semihbkgr.nettyims;

import com.semihbkgr.nettyims.ws.HttpInitializer;
import com.semihbkgr.nettyims.zk.ZKConnectionImpl;
import com.semihbkgr.nettyims.zk.ZKManagerImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class NettyIMSApp {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        var zkConn = new ZKConnectionImpl("localhost:2181");
        zkConn.connect(3000)
                .sync();

        var zkManager = new ZKManagerImpl(zkConn);

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
