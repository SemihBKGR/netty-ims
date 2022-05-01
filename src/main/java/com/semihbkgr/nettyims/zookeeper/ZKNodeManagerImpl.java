package com.semihbkgr.nettyims.zookeeper;

import lombok.NonNull;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;

@Singleton
public class ZKNodeManagerImpl implements ZKNodeManager {

    private final ZKConnection zkConn;

    @Inject
    public ZKNodeManagerImpl(@NonNull ZKConnection zkConn) {
        this.zkConn = zkConn;
    }

    @Override
    public void create(String path, String data) throws KeeperException, InterruptedException {
        zkConn.getZK()
                .create(
                        path,
                        data.getBytes(StandardCharsets.UTF_8),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
    }

    @Override
    public String get(String path) throws KeeperException, InterruptedException {
        var bytes = zkConn.getZK()
                .getData(path, null, null);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void delete(String path) throws KeeperException, InterruptedException {
        var version = zkConn.getZK()
                .exists(path, true)
                .getVersion();
        zkConn.getZK()
                .delete(path, version);
    }

}
