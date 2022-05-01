package com.semihbkgr.nettyims.user;

import lombok.NonNull;

import java.util.concurrent.atomic.AtomicInteger;

public class SequentialUsernameGenerator implements UsernameGenerator {

    private final AtomicInteger sequenceAtomicInteger;

    private final String base;

    public SequentialUsernameGenerator(@NonNull String base) {
        this.sequenceAtomicInteger = new AtomicInteger(0);
        this.base = base;
    }

    @Override
    public String username() {
        return base + sequenceAtomicInteger.incrementAndGet();
    }

}
