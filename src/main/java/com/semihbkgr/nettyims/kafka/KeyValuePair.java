package com.semihbkgr.nettyims.kafka;

import lombok.NonNull;

public class KeyValuePair {

    public final String key;
    public final String value;

    private KeyValuePair(@NonNull String key, @NonNull String value) {
        this.key = key;
        this.value = value;
    }

    public static KeyValuePair to(String key, String value) {
        return new KeyValuePair(key, value);
    }

}
