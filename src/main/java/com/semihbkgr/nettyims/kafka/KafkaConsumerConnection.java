package com.semihbkgr.nettyims.kafka;

public interface KafkaConsumerConnection extends AutoCloseable {

    KeyValuePair consume();

}
