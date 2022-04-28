package com.semihbkgr.nettyims.message;

import lombok.Data;

import java.util.List;

@Data
public class Message {

    private String id;

    private String content;

    private String from;

    private List<String> toList;

    private long timestamp;

}
