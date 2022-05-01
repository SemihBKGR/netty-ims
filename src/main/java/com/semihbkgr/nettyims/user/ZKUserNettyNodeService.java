package com.semihbkgr.nettyims.user;

import com.semihbkgr.nettyims.zookeeper.ZKNodeManagerImpl;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Slf4j
@Singleton
public class ZKUserNettyNodeService extends AbstractUserNettyNodeInstanceService implements UserNettyNodeSearchService {

    public static final String ZK_USERS_PATH = "/users";

    private final ZKNodeManagerImpl zkNodeManager;

    @Inject
    public ZKUserNettyNodeService(@NonNull String nodeId, @NonNull ZKNodeManagerImpl zkNodeManager) {
        super(nodeId);
        this.zkNodeManager = zkNodeManager;
    }

    @Override
    public String findNodeId(String username) {
        try {
            var nodeId = zkNodeManager.get(ZK_USERS_PATH + "/" + username);
            log.info("findNodeId - username: {}, nodeId: {}", username, nodeId);
            return nodeId;
        } catch (KeeperException | InterruptedException e) {
            log.warn("findNodeId - username: {}, exceptionMessage: {}", username, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean addUser(String username) {
        try {
            zkNodeManager.create(ZK_USERS_PATH + "/" + username, nodeId());
            log.info("addUser - username: {}", username);
            return true;
        } catch (KeeperException | InterruptedException e) {
            log.warn("addUser - username: {}, exceptionMessage: {}", username, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeUser(String username) {
        try {
            zkNodeManager.delete(ZK_USERS_PATH + "/" + username);
            log.info("removeUser - username: {}", username);
            return true;
        } catch (KeeperException | InterruptedException e) {
            log.warn("removeUser - username: {}, exceptionMessage: {}", username, e.getMessage());
            return false;
        }
    }

}
