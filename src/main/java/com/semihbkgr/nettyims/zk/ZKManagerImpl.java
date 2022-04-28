package com.semihbkgr.nettyims.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

public class ZKManagerImpl implements ZKManager {

    private final ZKConnection zkConn;

    public ZKManagerImpl(ZKConnection zkConn) {
        this.zkConn = zkConn;
    }

    @Override
    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zkConn.getZK()
                .create(
                        path,
                        data,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
    }

    @Override
    public boolean exists(String path) throws KeeperException, InterruptedException {
        return zkConn.getZK()
                .exists(path, false) != null;
    }

    @Override
    public Object get(String path) throws KeeperException, InterruptedException {
        byte[] b = zkConn.getZK()
                .getData(path, null, null);
        return new String(b);
    }

    @Override
    public void delete(String path) throws KeeperException, InterruptedException {
        int version = zkConn.getZK()
                .exists(path, true)
                .getVersion();
        zkConn.getZK()
                .delete(path, version);
    }

}
