package com.semihbkgr.nettyims;

import com.semihbkgr.nettyims.zookeeper.ZKManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import java.nio.charset.StandardCharsets;

@Slf4j
public class ZKNodeDataManager implements NodeDataManager {

    private final String nodeId;
    private final ZKManager zkManager;

    public ZKNodeDataManager(String nodeId, ZKManager zkManager) throws InterruptedException, KeeperException {
        this.nodeId = nodeId;
        this.zkManager = zkManager;
        if (!zkManager.exists( NodeDataManager.USERS_PATH)) {
            zkManager.create( NodeDataManager.USERS_PATH, new byte[]{});
        }
    }

    @Override
    public String getSessionNodeId(String username) throws InterruptedException, KeeperException {
        return (String) zkManager.get("/" + NodeDataManager.USERS_PATH + "/" + username);
    }

    @Override
    public void setSessionNodeId(String username) throws InterruptedException, KeeperException {
        zkManager.create("/" + NodeDataManager.USERS_PATH + "/" + username, username.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void deleteSessionNodeId(String username) throws InterruptedException, KeeperException {
        zkManager.delete("/" + NodeDataManager.USERS_PATH + "/" + username);
    }

}
