package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihbkgr.nettyims.kafka.KafkaConsumerConnection;
import com.semihbkgr.nettyims.kafka.KafkaProducerConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.function.BiConsumer;

@Slf4j
public class KafkaMessageHandler implements MessageHandler {

    private final ObjectMapper objectMapper;
    private final KafkaConsumerConnection kafkaConsumerConnection;
    private final KafkaProducerConnection kafkaProducerConnection;

    private final BiConsumer<? super String, ? super Message> messageConsumer;

    public KafkaMessageHandler(KafkaConsumerConnection kafkaConsumerConnection,
                               KafkaProducerConnection kafkaProducerConnection,
                               BiConsumer<? super String, ? super Message> messageConsumer) {
        this.kafkaConsumerConnection = kafkaConsumerConnection;
        this.kafkaProducerConnection = kafkaProducerConnection;
        this.objectMapper = new ObjectMapper();
        var kafkaConsumerEventThread = new Thread(() -> {
            while (true) {
                try {
                    var kvPair = kafkaConsumerConnection.consume();
                    var message = objectMapper.readValue(kvPair.value, Message.class);
                    onReceiveMessage(kvPair.key, message);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        kafkaConsumerEventThread.setName("KafkaConsumerEventThread");
        kafkaConsumerEventThread.setDaemon(true);
        kafkaConsumerEventThread.start();
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void onBroadcastMessage(String username, Message message) {
        log.info("onBroadcastMessage - username: {}, message: {}", username, message);
        try {
            var messageStr = objectMapper.writeValueAsString(message);
            kafkaProducerConnection.produce(username, messageStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveMessage(String username, Message message) {
        log.info("onReceiveMessage - username: {}, message: {}", username, message);
        messageConsumer.accept(username, message);
    }

}
