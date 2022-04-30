package com.semihbkgr.nettyims.kafka;

import com.semihbkgr.nettyims.NettyIMSApp;
import com.semihbkgr.nettyims.message.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
public class MessageReceiverImpl implements MessageReceiver {

    private final Consumer<String,String> consumer;
    private final MessageHandler messageHandler;

    public MessageReceiverImpl(MessageHandler messageHandler) {
        this.messageHandler=messageHandler;
        Properties properties = new Properties ();
        properties.setProperty("bootstrap.servers", "127.0.0.1:29092");
        properties.setProperty("batch.size", "16384");
        properties.setProperty ("linger.ms","1");
        properties.setProperty ("buffer.memory","33554432");
        properties.setProperty("group.id","receiver");
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
                    log.info("Consumer - {}", record.value());
                    messageHandler.onReceived(record.key(), record.value());
                }
            }
        }).start();
    }

}
