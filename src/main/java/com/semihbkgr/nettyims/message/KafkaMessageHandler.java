package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihbkgr.nettyims.kafka.KafkaConsumerConnection;
import com.semihbkgr.nettyims.kafka.KafkaProducerConnection;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

@Slf4j
public class KafkaMessageHandler implements MessageHandler {

    private final ObjectMapper objectMapper;
    private final KafkaConsumerConnection kafkaConsumerConnection;
    private final KafkaProducerConnection kafkaProducerConnection;

    private final CopyOnWriteArrayList<BiConsumer<? super String, ? super Message>> onReceiveMessageListenerList;

    public KafkaMessageHandler(KafkaConsumerConnection kafkaConsumerConnection,
                               KafkaProducerConnection kafkaProducerConnection) {
        this.kafkaConsumerConnection = kafkaConsumerConnection;
        this.kafkaProducerConnection = kafkaProducerConnection;
        this.objectMapper = new ObjectMapper();
        this.onReceiveMessageListenerList = new CopyOnWriteArrayList<>();
        var kafkaConsumerEventThread = new Thread(() -> {
            while (true) {
                try {
                    var kvPair = kafkaConsumerConnection.consume();
                    var message = objectMapper.readValue(kvPair.value, Message.class);
                    onReceiveMessageListenerList.forEach(listener -> {
                        listener.accept(kvPair.key, message);
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        kafkaConsumerEventThread.setName("KafkaConsumerEventThread");
        kafkaConsumerEventThread.setDaemon(true);
        kafkaConsumerEventThread.start();
    }

    @Override
    public void broadcastMessage(String receiver, Message message) {
        log.info("onBroadcastMessage - receiver: {}, message: {}", receiver, message);
        try {
            var messageStr = objectMapper.writeValueAsString(message);
            kafkaProducerConnection.produce(receiver, messageStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addOnReceiveMessageListener(@NonNull BiConsumer<? super String, ? super Message> listener) {
        onReceiveMessageListenerList.add(listener);
    }

}
