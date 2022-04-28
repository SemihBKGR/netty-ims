package com.semihbkgr.nettyims.zk;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public interface ZKConnection {

    ZKConnection connect(int sessionTimeout) throws IOException;

    ZooKeeper sync() throws InterruptedException;

    ZooKeeper getZK();

    boolean isConnected();

    boolean isAlive();

}
