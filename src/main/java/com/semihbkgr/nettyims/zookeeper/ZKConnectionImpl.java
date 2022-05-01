package com.semihbkgr.nettyims.zookeeper;

import lombok.NonNull;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@Singleton
public class ZKConnectionImpl implements ZKConnection {

    private ZooKeeper zk;
    private final CountDownLatch connectionLatch;

    private final String connectionString;

    @Inject
    public ZKConnectionImpl(@NonNull @Named("zkConnectionString") String connectionString) {
        this.connectionString = connectionString;
        this.connectionLatch = new CountDownLatch(1);
    }

    @Override
    public ZKConnection connect() throws IOException {
        synchronized (this) {
            if (zk == null) {
                synchronized (this) {
                    zk = new ZooKeeper(connectionString, ZKConnection.SESSION_TIMEOUT, we -> {
                        if (we.getState() == Watcher.Event.KeeperState.SyncConnected) {
                            connectionLatch.countDown();
                        }
                    });
                }
            }
        }
        return this;
    }

    @Override
    public ZKConnection sync() throws InterruptedException {
        connectionLatch.await();
        return this;
    }

    @Override
    public ZooKeeper getZK() {
        return zk;
    }

    @Override
    public boolean isConnected() {
        if (zk == null) {
            return false;
        }
        return zk.getState().isConnected();
    }

    @Override
    public void disconnect() throws InterruptedException {
        zk.close();
    }

}
