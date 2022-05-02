package com.semihbkgr.nettyims.kafka;

public interface KafkaProducerConnection extends AutoCloseable {

    void produce(String topic, String key, String value);

}
