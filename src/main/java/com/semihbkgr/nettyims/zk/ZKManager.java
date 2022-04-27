package com.semihbkgr.nettyims.zk;

import org.apache.zookeeper.KeeperException;

public interface ZKManager {

    void create(String path, byte[] data) throws KeeperException, InterruptedException;

    Object getZNodeData(String path, boolean watchFlag) throws KeeperException, InterruptedException;

    void update(String path, byte[] data) throws KeeperException, InterruptedException;

}