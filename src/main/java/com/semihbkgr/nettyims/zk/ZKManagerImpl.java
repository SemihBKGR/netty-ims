package com.semihbkgr.nettyims.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZKManagerImpl implements ZKManager {

    private static ZooKeeper zkeeper;
    private static ZKConnection zkConnection;

    @Override
    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zkeeper.create(
                path,
                data,
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
    }

    @Override
    public Object getZNodeData(String path, boolean watchFlag) throws KeeperException, InterruptedException {
        byte[] b = null;
        b = zkeeper.getData(path, null, null);
        return new String(b);
    }

    @Override
    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        int version = zkeeper.exists(path, true).getVersion();
        zkeeper.setData(path, data, version);
    }

}
