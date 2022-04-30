package com.semihbkgr.nettyims.kafka;

import com.semihbkgr.nettyims.message.Message;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class MessageBroadcasterImpl implements MessageBroadcaster {

    private final Producer<String,String> producer;

    public MessageBroadcasterImpl() {
        Properties properties = new Properties ();
        properties.setProperty("bootstrap.servers", "127.0.0.1:29092");
        properties.setProperty("batch.size", "16384");
        properties.setProperty ("linger.ms","1");
        properties.setProperty ("buffer.memory","33554432");
        properties.setProperty ("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty ("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(properties);
    }

    @Override
    public void broadcast(BroadcastData broadcastData) {
        producer.send(new ProducerRecord<>(broadcastData.getNodeId(),broadcastData.getUsername(),broadcastData.getMessage()));
    }

}
