package com.semihbkgr.nettyims.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaConsumerConnection extends AutoCloseable {

    ConsumerRecord<String, String> consume() throws InterruptedException;

}
