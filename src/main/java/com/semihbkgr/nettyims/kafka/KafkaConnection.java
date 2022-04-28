package com.semihbkgr.nettyims.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaConnection {

    private final Producer<String,String> producer;

    public KafkaConnection() {
        Properties properties = new Properties ();
        properties.setProperty ("bootstrap.servers","127.0.0.1:9092,127.0.0.1:9093,127.0.0.1:9094") ;
        properties.setProperty ("batch.size","16384");
        properties.setProperty ("linger.ms","1");
        properties.setProperty ("buffer.memory","33554432");
        properties.setProperty ("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty ("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<>(properties);
    }

    public void produce(String topic,String key,String value){
        producer.send(new ProducerRecord<>(topic,key,value));
    }

}
