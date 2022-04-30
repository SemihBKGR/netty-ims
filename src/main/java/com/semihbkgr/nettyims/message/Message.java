package com.semihbkgr.nettyims.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String id;

    private String content;

    private String from;

    private List<String> toList;

    private long timestamp;

}
