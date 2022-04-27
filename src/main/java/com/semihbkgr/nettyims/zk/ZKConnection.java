package com.semihbkgr.nettyims.zk;

import org.apache.zookeeper.ZooKeeper;

public interface ZKConnection {

    ZooKeeper getZK();

    ZooKeeper connect();

    void disconnect();

}
