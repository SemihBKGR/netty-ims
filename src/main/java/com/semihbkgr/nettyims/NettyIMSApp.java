package com.semihbkgr.nettyims;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.semihbkgr.nettyims.http.ChatWebSocketHandler;
import com.semihbkgr.nettyims.http.HttpInitializer;
import com.semihbkgr.nettyims.http.HttpServerHandler;
import com.semihbkgr.nettyims.kafka.KafkaConsumerConnection;
import com.semihbkgr.nettyims.kafka.KafkaConsumerConnectionImpl;
import com.semihbkgr.nettyims.kafka.KafkaProducerConnection;
import com.semihbkgr.nettyims.kafka.KafkaProducerConnectionImpl;
import com.semihbkgr.nettyims.message.KafkaMessageHandler;
import com.semihbkgr.nettyims.message.MessageHandler;
import com.semihbkgr.nettyims.message.WebSocketSenderOnReceiveMessageListener;
import com.semihbkgr.nettyims.user.*;
import com.semihbkgr.nettyims.user.comand.CommandProcessor;
import com.semihbkgr.nettyims.user.comand.CommandProcessorMultiplexer;
import com.semihbkgr.nettyims.user.comand.instance.HelpCommandProcessor;
import com.semihbkgr.nettyims.user.comand.instance.InfoCommandProcessor;
import com.semihbkgr.nettyims.user.comand.instance.ServerCommandProcessor;
import com.semihbkgr.nettyims.user.comand.instance.UsernameCommandProcessor;
import com.semihbkgr.nettyims.zookeeper.ZKConnection;
import com.semihbkgr.nettyims.zookeeper.ZKConnectionImpl;
import com.semihbkgr.nettyims.zookeeper.ZKNodeManager;
import com.semihbkgr.nettyims.zookeeper.ZKNodeManagerImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class NettyIMSApp {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        var serverNodeId = ServerNodeIdGenerator.RANDOM_NUMERICAL_GENERATOR.nodeId();
        log.info("NettyIMS - serverNodeId: {}", serverNodeId);
        EnvPropertyContracts.loadPropertiesFromEnv(serverNodeId);
        var injector = InjectModules.inject();

        var zkConnection = injector.getInstance(ZKConnection.class);
        zkConnection.connect().sync();
        zkConnection.getZK().create("/" + serverNodeId, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        var messageHandler = injector.getInstance(MessageHandler.class);
        var senderOnReceiverMessageListener = injector.getInstance(WebSocketSenderOnReceiveMessageListener.class);
        messageHandler.addOnReceiveMessageListener(senderOnReceiverMessageListener);

        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(injector.getInstance(HttpInitializer.class));
            Channel channel = bootstrap.bind(Integer.parseInt(System.getProperty(EnvPropertyContracts.PORT_SYSTEM_PROPERTY)))
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
    }

    public interface ServerNodeIdGenerator {

        int MIN_ID_LENGTH = 5;
        int MAX_ID_LENGTH = 32;
        int DEFAULT_ID_LENGTH = 9;

        ServerNodeIdGenerator RANDOM_NUMERICAL_GENERATOR = new RandomServerNodeIdGenerator(DEFAULT_ID_LENGTH);

        String nodeId();

        class RandomServerNodeIdGenerator implements ServerNodeIdGenerator {

            private final int length;
            private final Random random;

            private RandomServerNodeIdGenerator(int length) {
                this.length = Math.min(ServerNodeIdGenerator.MAX_ID_LENGTH, Math.max(ServerNodeIdGenerator.MIN_ID_LENGTH, length));
                this.random = new Random();
            }

            @Override
            public String nodeId() {
                return IntStream.range(0, length)
                        .mapToObj(i -> (char) (48 + random.nextInt(10)))
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();
            }

        }

    }

    static class EnvPropertyContracts {

        static final String SERVER_NODE_ID_SYSTEM_PROPERTY = "netty-ims.server-node-id";

        static final String PORT_ENV_VAR = "NETTY_IMS_PORT";
        static final String PORT_SYSTEM_PROPERTY = "netty-ims.port";
        static final int DEFAULT_PORT = 9000;

        static final String ZK_CONNECTION_STRING_ENV_VAR = "NETTY_IMS_ZK_CONNECTION_STRING";
        static final String ZK_CONNECTION_STRING_SYSTEM_PROPERTY = "netty-ims.zk-connection-string";


        static final String KAFKA_BOOTSTRAP_SERVERS_ENV_VAR = "NETTY_IMS_KAFKA_BOOTSTRAP_SERVERS";
        static final String KAFKA_BOOTSTRAP_SERVERS_SYSTEM_PROPERTY = "netty-ims.kafka-bootstrap-servers";

        private EnvPropertyContracts() {
        }

        static void loadPropertiesFromEnv(@NonNull String serverNodeId) {
            System.getProperties().setProperty(SERVER_NODE_ID_SYSTEM_PROPERTY, serverNodeId);
            System.getProperties().setProperty(PORT_SYSTEM_PROPERTY, String.valueOf(getEnvOrDefault(PORT_ENV_VAR, DEFAULT_PORT)));
            System.getProperties().setProperty(ZK_CONNECTION_STRING_SYSTEM_PROPERTY, getEnv(ZK_CONNECTION_STRING_ENV_VAR));
            System.getProperties().setProperty(KAFKA_BOOTSTRAP_SERVERS_SYSTEM_PROPERTY, getEnv(KAFKA_BOOTSTRAP_SERVERS_ENV_VAR));
        }

        static String getEnv(@NonNull String envVar) {
            var value = System.getenv(envVar);
            if (value != null) {
                return value;
            }
            throw new IllegalStateException(String.format("env var '%s' is required", envVar));
        }

        static String getEnvOrDefault(@NonNull String envVar, @NonNull String defVal) {
            var value = System.getenv(envVar);
            if (value != null) {
                return value;
            }
            return defVal;
        }

        static int getEnvOrDefault(@NonNull String envVar, int defVal) {
            var value = getEnvOrDefault(envVar, String.valueOf(defVal));
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            return defVal;
        }

    }

    static class InjectModules {

        private InjectModules() {
        }

        static Injector inject() {
            return Guice.createInjector(
                    new ZKInjectModule(),
                    new HttpInjectModules(),
                    new UserInjectModules(),
                    new CommandProcessorInjectModule(),
                    new MessageInjectModule(),
                    new KafkaInjectModules()
            );
        }

        static class ZKInjectModule extends AbstractModule {

            static final String ZK_CONNECTION_STRING_NAMED = "zkConnectionString";

            @Override
            protected void configure() {
                bind(String.class).annotatedWith(Names.named(ZK_CONNECTION_STRING_NAMED))
                        .toInstance(System.getProperty(EnvPropertyContracts.ZK_CONNECTION_STRING_SYSTEM_PROPERTY));
                bind(ZKConnection.class).to(ZKConnectionImpl.class).in(Singleton.class);
                bind(ZKNodeManager.class).to(ZKNodeManagerImpl.class).in(Singleton.class);
            }

        }

        static class HttpInjectModules extends AbstractModule {

            static final String SERVER_NODE_ID_NAMED = "serverNodeId";

            @Override
            protected void configure() {
                bind(HttpInitializer.class).in(Singleton.class);
                bind(String.class).annotatedWith(Names.named(SERVER_NODE_ID_NAMED))
                        .toInstance(System.getProperty(EnvPropertyContracts.SERVER_NODE_ID_SYSTEM_PROPERTY));
                bind(HttpServerHandler.class).in(Singleton.class);
                bind(ChatWebSocketHandler.class).in(Singleton.class);
            }

        }

        static class UserInjectModules extends AbstractModule {

            static final String USERNAME_GENERATOR_BASE = "usernameBase";

            @Override
            protected void configure() {
                bind(UserActionHandler.class).to(AsynchronousUserActionHandler.class).in(Singleton.class);
                bind(UserChannelContainer.class).to(ConcurrentUserChannelContainer.class).in(Singleton.class);
                bind(String.class).annotatedWith(Names.named(USERNAME_GENERATOR_BASE))
                        .toInstance("user-" + System.getProperty(EnvPropertyContracts.SERVER_NODE_ID_SYSTEM_PROPERTY) + "-");
                bind(UsernameGenerator.class).to(SequentialUsernameGenerator.class).in(Singleton.class);
                bind(ZKUserNettyNodeService.class).in(Singleton.class);
                bind(UserNettyNodeSearchService.class).to(ZKUserNettyNodeService.class);
                bind(UserNettyNodeInstanceService.class).to(ZKUserNettyNodeService.class);
            }

        }

        static class CommandProcessorInjectModule extends AbstractModule {

            static final String HELP_COMMAND_PREFIX_NAMED = "helpCommandPrefix";
            static final String HELP_COMMAND_PREFIX = "/help";
            static final String HELP_COMMAND_SENDER_NAMED = "helpCommandSender";
            static final String HELP_COMMAND_SENDER = "server";

            static final String INFO_COMMAND_PREFIX_NAMED = "infoCommandPrefix";
            static final String INFO_COMMAND_PREFIX = "/info";
            static final String INFO_COMMAND_SENDER_NAMED = "infoCommandSender";
            static final String INFO_COMMAND_SENDER = "server";

            static final String SERVER_COMMAND_PREFIX_NAMED = "serverCommandPrefix";
            static final String SERVER_COMMAND_PREFIX = "/server";
            static final String SERVER_COMMAND_SENDER_NAMED = "serverCommandSender";
            static final String SERVER_COMMAND_SENDER = "server";

            static final String USERNAME_COMMAND_PREFIX_NAMED = "usernameCommandPrefix";
            static final String USERNAME_COMMAND_PREFIX = "/username";
            static final String USERNAME_COMMAND_SENDER_NAMED = "usernameCommandSender";
            static final String USERNAME_COMMAND_SENDER = "server";

            @Override
            protected void configure() {
                Multibinder<CommandProcessor> commandProcessorMultibinder = Multibinder.newSetBinder(binder(), CommandProcessor.class);
                bind(String.class).annotatedWith(Names.named(HELP_COMMAND_PREFIX_NAMED)).toInstance(HELP_COMMAND_PREFIX);
                bind(String.class).annotatedWith(Names.named(HELP_COMMAND_SENDER_NAMED)).toInstance(HELP_COMMAND_SENDER);
                commandProcessorMultibinder.addBinding().to(HelpCommandProcessor.class);
                bind(String.class).annotatedWith(Names.named(INFO_COMMAND_PREFIX_NAMED)).toInstance(INFO_COMMAND_PREFIX);
                bind(String.class).annotatedWith(Names.named(INFO_COMMAND_SENDER_NAMED)).toInstance(INFO_COMMAND_SENDER);
                commandProcessorMultibinder.addBinding().to(InfoCommandProcessor.class);
                bind(String.class).annotatedWith(Names.named(SERVER_COMMAND_PREFIX_NAMED)).toInstance(SERVER_COMMAND_PREFIX);
                bind(String.class).annotatedWith(Names.named(SERVER_COMMAND_SENDER_NAMED)).toInstance(SERVER_COMMAND_SENDER);
                commandProcessorMultibinder.addBinding().to(ServerCommandProcessor.class);
                bind(String.class).annotatedWith(Names.named(USERNAME_COMMAND_PREFIX_NAMED)).toInstance(USERNAME_COMMAND_PREFIX);
                bind(String.class).annotatedWith(Names.named(USERNAME_COMMAND_SENDER_NAMED)).toInstance(USERNAME_COMMAND_SENDER);
                commandProcessorMultibinder.addBinding().to(UsernameCommandProcessor.class);
                bind(CommandProcessorMultiplexer.class).in(Singleton.class);
            }

        }

        static class MessageInjectModule extends AbstractModule {

            @Override
            protected void configure() {
                bind(MessageHandler.class).to(KafkaMessageHandler.class).in(Singleton.class);
                bind(WebSocketSenderOnReceiveMessageListener.class).in(Singleton.class);
            }

        }

        static class KafkaInjectModules extends AbstractModule {

            static final String KAFKA_BOOTSTRAP_SERVERS_NAMED = "kafkaBootstrapServers";
            static final String KAFKA_CONSUMER_TOPIC_LIST_NAMED = "kafkaConsumerTopicList";
            static final String KAFKA_COMMON_MESSAGE_TOPIC_NAMED = "commonMessageTopic";
            static final String KAFKA_COMMON_MESSAGE_TOPIC = "common";
            static final String KAFKA_CONSUMER_GROUP_ID_NAMED = "kafkaConsumerGroupId";
            static final String KAFKA_CONSUMER_GROUP_ID_PREFIX = "sender";

            @Override
            protected void configure() {
                bind(String.class).annotatedWith(Names.named(KAFKA_BOOTSTRAP_SERVERS_NAMED))
                        .toInstance(System.getProperty(EnvPropertyContracts.KAFKA_BOOTSTRAP_SERVERS_SYSTEM_PROPERTY));
                bind(KafkaProducerConnection.class).to(KafkaProducerConnectionImpl.class).in(Singleton.class);
                bind(new TypeLiteral<List<String>>() {
                }).annotatedWith(Names.named(KAFKA_CONSUMER_TOPIC_LIST_NAMED))
                        .toInstance(List.of(System.getProperty(EnvPropertyContracts.SERVER_NODE_ID_SYSTEM_PROPERTY), KAFKA_COMMON_MESSAGE_TOPIC));
                bind(String.class).annotatedWith(Names.named(KAFKA_COMMON_MESSAGE_TOPIC_NAMED))
                        .toInstance(KAFKA_COMMON_MESSAGE_TOPIC);
                bind(String.class).annotatedWith(Names.named(KAFKA_CONSUMER_GROUP_ID_NAMED))
                        .toInstance(KAFKA_CONSUMER_GROUP_ID_PREFIX + '-' + System.getProperty(EnvPropertyContracts.SERVER_NODE_ID_SYSTEM_PROPERTY));
                bind(KafkaConsumerConnection.class).to(KafkaConsumerConnectionImpl.class).in(Singleton.class);
            }

        }

    }

}
