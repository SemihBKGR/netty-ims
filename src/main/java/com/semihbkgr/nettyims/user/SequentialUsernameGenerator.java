package com.semihbkgr.nettyims.user;

import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class SequentialUsernameGenerator implements UsernameGenerator {

    private final AtomicInteger sequenceAtomicInteger;

    private final String base;

    @Inject
    public SequentialUsernameGenerator(@NonNull @Named("usernameBase") String base) {
        this.sequenceAtomicInteger = new AtomicInteger(0);
        this.base = base;
    }

    @Override
    public String username() {
        return base + sequenceAtomicInteger.incrementAndGet();
    }

}
