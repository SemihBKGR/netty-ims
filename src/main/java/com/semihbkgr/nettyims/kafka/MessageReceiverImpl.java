package com.semihbkgr.nettyims.kafka;

import com.semihbkgr.nettyims.NettyIMSApp;
import com.semihbkgr.nettyims.message.MessageHandler;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class MessageReceiverImpl implements MessageReceiver {

    private final Consumer<String,String> consumer;
    private final MessageHandler messageHandler;

    public MessageReceiverImpl(MessageHandler messageHandler) {
        this.messageHandler=messageHandler;
        Properties properties = new Properties ();
        properties.setProperty("bootstrap.servers", "127.0.0.1:29092,127.0.0.1:29093,127.0.0.1:29094");
        properties.setProperty("batch.size", "16384");
        properties.setProperty ("linger.ms","1");
        properties.setProperty ("buffer.memory","33554432");
        properties.setProperty ("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty ("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        this.consumer = new KafkaConsumer<>(properties);
    }

    @Override
    public void startReceiving(String noteId) {
        new Thread(()->{
            consumer.subscribe(List.of(NettyIMSApp.NODE_ID));
            while(true){
                ConsumerRecords<String,String> records=consumer.poll(Duration.ofMillis(100));
                for(ConsumerRecord<String,String> record: records){
                    messageHandler.onReceived(record.key(),record.value());
                }
            }
        }).start();
    }

}
