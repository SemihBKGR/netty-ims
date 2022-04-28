package com.semihbkgr.nettyims;

import org.apache.zookeeper.KeeperException;

public interface NodeDataManager {

    String USERS_PATH="/users";

    String getSessionNodeId(String username) throws InterruptedException, KeeperException;

    void setSessionNodeId(String username) throws InterruptedException, KeeperException;

    void deleteSessionNodeId(String username) throws InterruptedException, KeeperException;

}
