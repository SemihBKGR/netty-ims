package com.semihbkgr.nettyims.user;

import java.util.concurrent.atomic.AtomicInteger;

public class SequentialUsernameGenerator implements UsernameGenerator {

    private final AtomicInteger sequenceAtomicInteger;

    private final String base;

    public SequentialUsernameGenerator(String base) {
        this.sequenceAtomicInteger = new AtomicInteger(0);
        this.base = base;
    }

    @Override
    public String generate() {
        return base + sequenceAtomicInteger.incrementAndGet();
    }

}
