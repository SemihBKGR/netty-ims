package com.semihbkgr.nettyims.user.comand;

public interface CommandProcessor {

    boolean isCommand(String msg);

    void process(String msg);

}
