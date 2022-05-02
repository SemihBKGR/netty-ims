package com.semihbkgr.nettyims.kafka;

import lombok.NonNull;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Properties;

@Singleton
public class KafkaProducerConnectionImpl implements KafkaProducerConnection {

    private final Producer<String, String> producer;

    @Inject
    public KafkaProducerConnectionImpl(@NonNull @Named("kafkaBootstrapServers") String bootstrapServers) {
        var properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrapServers);
        properties.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(properties);
    }

    @Override
    public void produce(@NonNull String topic, @NonNull String key, @NonNull String value) {
        producer.send(new ProducerRecord<>(topic, key, value));
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }

}
