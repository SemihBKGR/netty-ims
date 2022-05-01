package com.semihbkgr.nettyims.zookeeper;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public interface ZKConnection {

    int SESSION_TIMEOUT=3000;

    ZKConnection connect() throws IOException;

    ZKConnection sync() throws InterruptedException;

    ZooKeeper getZK();

    boolean isConnected();

    void disconnect() throws InterruptedException;

}
