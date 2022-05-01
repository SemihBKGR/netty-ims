package com.semihbkgr.nettyims;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.semihbkgr.nettyims.user.UserActionHandler;
import com.semihbkgr.nettyims.websocket.HttpInitializer;
import com.semihbkgr.nettyims.websocket.HttpServerHandler;
import com.semihbkgr.nettyims.websocket.WebSocketHandler;
import com.semihbkgr.nettyims.zookeeper.ZKConnection;
import com.semihbkgr.nettyims.zookeeper.ZKConnectionImpl;
import com.semihbkgr.nettyims.zookeeper.ZKNodeManager;
import com.semihbkgr.nettyims.zookeeper.ZKNodeManagerImpl;

import java.util.Random;
import java.util.stream.IntStream;

public class NettyIMSApp {

    public static final String PORT_ENV_VAR = "NETTY_IMS_PORT";
    public static final int DEFAULT_PORT = 9000;

    public static void main(String[] args) {

        var injector = InjectModules.inject();

        /*
        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpInitializer());
            Channel channel = bootstrap.bind(getPort())
                    .sync()
                    .channel();
            channel.closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
        */
    }

    public static int getPort() {
        var portStr = System.getenv(PORT_ENV_VAR);
        if (portStr != null) {
            try {
                return Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                return DEFAULT_PORT;
            }
        }
        return DEFAULT_PORT;
    }

    public interface NodeIdGenerator {

        int MIN_ID_LENGTH = 5;
        int MAX_ID_LENGTH = 32;
        int DEFAULT_ID_LENGTH = 9;

        NodeIdGenerator RANDOM_GENERATOR = new RandomNodeIdGenerator(DEFAULT_ID_LENGTH);

        String id();

        class RandomNodeIdGenerator implements NodeIdGenerator {

            private final int length;
            private final Random random;

            private RandomNodeIdGenerator(int length) {
                this.length = Math.min(NodeIdGenerator.MAX_ID_LENGTH, Math.max(NodeIdGenerator.MIN_ID_LENGTH, length));
                this.random = new Random();
            }

            @Override
            public String id() {
                return IntStream.range(0, length)
                        .mapToObj(i -> (char) (42 + random.nextInt(10)))
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
            }

        }

    }

    public static class InjectModules {

        public static class ZKInjectModule extends AbstractModule {

            @Override
            protected void configure() {
                bind(String.class).annotatedWith(Names.named("zkConnectionString")).toInstance("127.0.0.1:2181;127.0.0.1:2182;127.0.0.1:2183");
                bind(ZKConnection.class).to(ZKConnectionImpl.class);
                bind(ZKNodeManager.class).to(ZKNodeManagerImpl.class);
            }

        }

        public static class WebSocketInjectModules extends AbstractModule {

            @Override
            protected void configure() {
                bind(HttpInitializer.class).to(HttpInitializer.class);
                bind(HttpServerHandler.class).to(HttpServerHandler.class);
                bind(WebSocketHandler.class).to(WebSocketHandler.class);
            }

        }

        public static class UserInjectModules extends AbstractModule{

            @Override
            protected void configure() {
                bind(UserActionHandler)
            }

        }

        public static Injector inject() {
            return Guice.createInjector(
                    new ZKInjectModule(),
                    new WebSocketInjectModules(),
                    new UserInjectModules()
            );
        }

    }

}
