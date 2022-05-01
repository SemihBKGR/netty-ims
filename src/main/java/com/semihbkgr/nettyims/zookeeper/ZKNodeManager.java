package com.semihbkgr.nettyims.zookeeper;

import org.apache.zookeeper.KeeperException;

public interface ZKNodeManager {

    void create(String path, String data) throws KeeperException, InterruptedException;

    String get(String path) throws KeeperException, InterruptedException;

    void delete(String path) throws KeeperException, InterruptedException;

}