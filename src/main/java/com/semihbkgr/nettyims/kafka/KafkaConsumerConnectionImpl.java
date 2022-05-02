package com.semihbkgr.nettyims.kafka;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Singleton
public class KafkaConsumerConnectionImpl implements KafkaConsumerConnection {

    private final KafkaConsumer<String, String> consumer;
    private final Thread consumerThread;
    private final LinkedBlockingQueue<ConsumerRecord<String, String>> valueQueue;

    @Inject
    public KafkaConsumerConnectionImpl(@NonNull @Named("kafkaBootstrapServers") String bootstrapServers,
                                       @NonNull @Named("kafkaConsumerTopicList") List<String> topicList,
                                       @NonNull @Named("kafkaConsumerGroupId") String groupId) {
        var properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("group.id", groupId);
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(properties);
        this.valueQueue = new LinkedBlockingQueue<>();
        this.consumerThread = new Thread(() -> {
            consumer.subscribe(topicList);
            while (!Thread.interrupted()) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));
                consumerRecords.forEach(cr -> {
                    log.info("KafkaConsumer - topic: '{}', key: '{}', value: '{}'", cr.topic(), cr.key(), cr.value());
                    try {
                        valueQueue.put(cr);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        consumerThread.setName("KafkaConsumerThread");
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    @Override
    public ConsumerRecord<String, String> consume() throws InterruptedException {
        return valueQueue.take();
    }

    @Override
    public void close() {
        consumerThread.interrupt();
        consumer.close();
    }

}
