package com.semihbkgr.nettyims.kafka;

import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class KafkaConsumerConnectionImpl implements KafkaConsumerConnection {

    private final KafkaConsumer<String, String> consumer;
    private final Thread consumerThread;
    private final LinkedBlockingQueue<KeyValuePair> valueQueue;

    @Inject
    public KafkaConsumerConnectionImpl(@NonNull @Named("kafkaConsumerBootstrapServer") String bootstrapServer,
                                       @NonNull @Named("kafkaConsumerGroupId") String groupId,
                                       @NonNull @Named("kafkaConsumerGroup") String topic) {
        var properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServer);
        properties.setProperty("group.id", groupId);
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(properties);
        this.valueQueue = new LinkedBlockingQueue<>();
        this.consumerThread = new Thread(() -> {
            consumer.subscribe(List.of(topic));
            while (!Thread.interrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                records.iterator().forEachRemaining(v -> {
                    try {
                        valueQueue.put(KeyValuePair.to(v.key(), v.value()));
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
    public KeyValuePair consume() {
        return valueQueue.poll();
    }

    @Override
    public void close() throws Exception {
        consumerThread.interrupt();
        consumer.close();
    }

}
