package com.semihbkgr.nettyims.zookeeper;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ZKConnectionImpl implements ZKConnection {

    private ZooKeeper zk;
    private final CountDownLatch connectionLatch;

    private final List<String> connectionStringList;

    public ZKConnectionImpl(List<String> connectionStringList) throws IOException {
        this.connectionStringList = connectionStringList;
        connectionLatch = new CountDownLatch(1);
    }

    public ZKConnectionImpl(String connectionString) throws IOException {
        this(List.of(connectionString));
    }

    public ZKConnectionImpl(Map<String, ? extends List<Integer>> hostnamePortsMap) throws IOException {
        this(ZKUtil.connectionStrings(hostnamePortsMap));
    }

    @Override
    public ZKConnection connect(int sessionTimeout) throws IOException {
        if (zk == null) {
            zk = new ZooKeeper(connectionStringList.get(0), sessionTimeout, we -> {
                        if (we.getState() == Watcher.Event.KeeperState.SyncConnected) {
                            connectionLatch.countDown();
                        }
                    });
                }
        return this;
    }

    @Override
    public ZooKeeper sync() throws InterruptedException {
        connectionLatch.await();
        return zk;
    }

    @Override
    public ZooKeeper getZK() {
        return zk;
    }

    @Override
    public boolean isConnected() {
        return zk.getState().isConnected();
    }

    @Override
    public boolean isAlive() {
        return zk.getState().isAlive();
    }

}
