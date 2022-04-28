package com.semihbkgr.nettyims.kafka;

import com.semihbkgr.nettyims.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BroadcastData {

    private String nodeId;

    private String username;

    private String message;

}
