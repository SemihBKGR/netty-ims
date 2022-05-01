package com.semihbkgr.nettyims.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class ZKConnectionImplTest {

    @Test
    void test() throws IOException, InterruptedException, KeeperException {

        var c = new ZKConnectionImpl("localhost:2181").connect().sync();

        System.out.println(c.getZK().getSessionId());
        System.out.println("-----");
        c.getZK().getChildren("/", false).stream().forEach(System.out::println);

        var m=new ZKNodeManagerImpl(c);
        m.create("/test","".getBytes(StandardCharsets.UTF_8));
        System.out.println("1");
        m.create("/test","".getBytes(StandardCharsets.UTF_8));
        System.out.println(2);

    }


}