package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihbkgr.nettyims.kafka.KafkaConsumerConnection;
import com.semihbkgr.nettyims.kafka.KafkaProducerConnection;
import com.semihbkgr.nettyims.user.UserNettyNodeSearchService;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

@Singleton
public class KafkaMessageHandler implements MessageHandler {

    private static final String COMMON_MESSAGE_KAFKA_TOPIC = "";
    private final ObjectMapper objectMapper;
    private final KafkaProducerConnection kafkaProducerConnection;

    private final UserNettyNodeSearchService userNettyNodeSearchService;

    private final String commonMessageTopic;
    private final CopyOnWriteArrayList<BiConsumer<List<String>, Message>> onReceiveMessageListenerList;

    @Inject
    public KafkaMessageHandler(@NonNull ObjectMapper objectMapper,
                               @NonNull KafkaProducerConnection kafkaProducerConnection,
                               @NonNull UserNettyNodeSearchService userNettyNodeSearchService,
                               @NonNull @Named("commonMessageTopic") String commonMessageTopic,
                               @NonNull KafkaConsumerConnection kafkaConsumerConnection) {
        this.objectMapper = objectMapper;
        this.kafkaProducerConnection = kafkaProducerConnection;
        this.userNettyNodeSearchService = userNettyNodeSearchService;
        this.commonMessageTopic = commonMessageTopic;
        this.onReceiveMessageListenerList = new CopyOnWriteArrayList<>();
        var kafkaConsumerEventThread = new Thread(() -> {
            while (true) {
                try {
                    var consumerRecord = kafkaConsumerConnection.consume();
                    var message = objectMapper.readValue(consumerRecord.value(), Message.class);
                    if (consumerRecord.key().equals(COMMON_MESSAGE_KAFKA_TOPIC)) {
                        onReceiveMessageListenerList.forEach(listener -> listener.accept(Collections.emptyList(), message));
                    } else {
                        var receiverUsernameList = Arrays.asList(consumerRecord.key().split(","));
                        onReceiveMessageListenerList.forEach(listener -> listener.accept(receiverUsernameList, message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        kafkaConsumerEventThread.setName("KafkaConsumerEventThread");
        kafkaConsumerEventThread.setDaemon(true);
        kafkaConsumerEventThread.start();
    }

    @Override
    public void broadcastMessage(@NonNull String from, @NonNull String messageStr) {
        try {
            var message = objectMapper.readValue(messageStr, Message.class);
            message.setId(UUID.randomUUID().toString());
            message.setFrom(from);
            message.setTimestamp(System.currentTimeMillis());
            var serializedMessage = objectMapper.writeValueAsString(message);
            if (message.getToList().isEmpty()) {
                kafkaProducerConnection.produce(commonMessageTopic, COMMON_MESSAGE_KAFKA_TOPIC, serializedMessage);
            } else {
                mapUsernamesToServerNodeId(message).entrySet()
                        .parallelStream()
                        .forEach(usernamesServerNodeId -> kafkaProducerConnection.produce(usernamesServerNodeId.getKey(), usernamesServerNodeId.getValue(), serializedMessage));
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOnReceiveMessageListener(@NonNull BiConsumer<List<String>, Message> listener) {
        onReceiveMessageListenerList.add(listener);
    }

    private Map<String, String> mapUsernamesToServerNodeId(@NonNull Message message) {
        var usernameServerNodeIdMap = new ConcurrentHashMap<String, String>();
        message.getToList()
                .parallelStream()
                .forEach(toUsername -> {
                    var serverNodeId = userNettyNodeSearchService.findNodeId(toUsername);
                    if (serverNodeId != null) {
                        usernameServerNodeIdMap.merge(serverNodeId, toUsername, (usernames, username) -> usernames + ',' + username);
                    }
                });
        return usernameServerNodeIdMap;
    }

}
