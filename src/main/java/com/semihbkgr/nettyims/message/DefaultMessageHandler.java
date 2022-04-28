package com.semihbkgr.nettyims.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semihbkgr.nettyims.NettyIMSApp;
import com.semihbkgr.nettyims.NodeDataManager;
import com.semihbkgr.nettyims.ZKNodeDataManager;
import com.semihbkgr.nettyims.kafka.BroadcastData;
import com.semihbkgr.nettyims.kafka.MessageBroadcaster;
import com.semihbkgr.nettyims.kafka.MessageBroadcasterImpl;
import com.semihbkgr.nettyims.websocket.DefaultUserWSChannelContainer;
import com.semihbkgr.nettyims.zookeeper.ZKConnectionImpl;
import com.semihbkgr.nettyims.zookeeper.ZKManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.List;

@Slf4j
public class DefaultMessageHandler implements MessageHandler {

    private final ObjectMapper objectMapper;
    private final MessageBroadcaster messageBroadcaster;
    private final NodeDataManager nodeDataManager;

    public DefaultMessageHandler() throws IOException, InterruptedException, KeeperException {
        this.objectMapper = new ObjectMapper();
        this.messageBroadcaster = new MessageBroadcasterImpl();
        var zkConn = new ZKConnectionImpl(List.of("127.0.0.1:2181", "127.0.0.1:2182", "127.0.0.1:2183"));
        zkConn.connect(3000).sync();
        var zkManager = new ZKManagerImpl(zkConn);
        this.nodeDataManager = new ZKNodeDataManager(NettyIMSApp.NODE_ID, zkManager);
    }

    @Override
    public void onUserConnected(String username) {

    }

    @Override
    public void onUserDisconnected(String username) {

    }

    @Override
    public void onSend(String username, String rawMessage) throws JsonProcessingException, InterruptedException, KeeperException {
        var msg = objectMapper.readValue(rawMessage, Message.class);
        msg.setFrom(username);
        msg.setTimestamp(System.currentTimeMillis());
        var nodeId = nodeDataManager.getSessionNodeId(username);
        var msgStr = objectMapper.writeValueAsString(msg);
        var broadcasterData = new BroadcastData(nodeId, username, msgStr);
        messageBroadcaster.broadcast(broadcasterData);
    }

    @Override
    public void onReceived(String username, String rawMessage) {
        var channel = DefaultUserWSChannelContainer.getInstance().getChannel(username);
        channel.writeAndFlush(rawMessage);
    }

}
