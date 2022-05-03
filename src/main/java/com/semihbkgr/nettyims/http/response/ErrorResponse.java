package com.semihbkgr.nettyims.http.response;

import lombok.Data;

@Data
public class ErrorResponse {

    private long timestamp;

    private String message;

    private String url;

    private int status;

}
