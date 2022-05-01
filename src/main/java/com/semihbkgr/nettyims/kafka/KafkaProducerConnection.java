package com.semihbkgr.nettyims.kafka;

public interface KafkaProducerConnection extends AutoCloseable {

    void produce(KeyValuePair keyValuePair);

    void produce(String key, String value);

}
