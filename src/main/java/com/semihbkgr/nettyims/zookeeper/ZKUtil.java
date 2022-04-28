package com.semihbkgr.nettyims.zookeeper;

import java.util.List;
import java.util.Map;

public class ZKUtil {

    private ZKUtil() {
    }

    public static String connectionString(String hostname, int port) {
        return String.format("%s:%d", hostname, port);
    }

    public static List<String> connectionStrings(Map<String, ? extends List<Integer>> hostnamePortsMap) {
        return hostnamePortsMap.entrySet()
                .stream()
                .flatMap(e -> e.getValue()
                        .stream()
                        .map(p -> connectionString(e.getKey(), p)))
                .toList();
    }

}
