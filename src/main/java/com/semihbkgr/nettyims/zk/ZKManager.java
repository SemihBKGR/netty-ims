package com.semihbkgr.nettyims.zk;

import org.apache.zookeeper.KeeperException;

public interface ZKManager {

    void create(String path, byte[] data) throws KeeperException, InterruptedException;

    boolean exists(String path) throws KeeperException, InterruptedException;

    Object get(String path) throws KeeperException, InterruptedException;

    void delete(String path) throws KeeperException, InterruptedException;

}