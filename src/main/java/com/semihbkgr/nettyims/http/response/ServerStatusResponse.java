package com.semihbkgr.nettyims.http.response;

import lombok.Data;

@Data
public class ServerStatusResponse {

    private String nodeId;

    private String address;

    private int usersCount;

}
