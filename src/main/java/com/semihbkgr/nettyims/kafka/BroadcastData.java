package com.semihbkgr.nettyims.kafka;

import com.semihbkgr.nettyims.message.Message;
import lombok.Data;

@Data
public class BroadcastData {

    private String nodeId;

    private String username;

    private Message message;

}
