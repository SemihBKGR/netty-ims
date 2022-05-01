package com.semihbkgr.nettyims.kafka;

import lombok.NonNull;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerConnectionImpl implements KafkaProducerConnection {

    private final Producer<String, String> producer;
    private final String topic;

    public KafkaProducerConnectionImpl(@NonNull String bootstrapServers, @NonNull String topic) {
        var properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(properties);
        this.topic = topic;
    }

    @Override
    public void produce(KeyValuePair keyValuePair) {
        produce(keyValuePair.key, keyValuePair.value);
    }

    @Override
    public void produce(@NonNull String key, @NonNull String value) {
        producer.send(new ProducerRecord<>(topic, value));
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }

}
