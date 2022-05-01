package com.semihbkgr.nettyims;

import java.util.Random;
import java.util.stream.IntStream;

public interface NettyNodeIdGenerator {

    int MIN_ID_LENGTH = 5;
    int MAX_ID_LENGTH = 32;
    int DEFAULT_ID_LENGTH = 9;

    NettyNodeIdGenerator RANDOM_GENERATOR = new RandomNettyNodeIdGenerator(DEFAULT_ID_LENGTH);

    String id();

    class RandomNettyNodeIdGenerator implements NettyNodeIdGenerator {

        private final int length;
        private final Random random;

        private RandomNettyNodeIdGenerator(int length) {
            this.length = Math.min(NettyNodeIdGenerator.MAX_ID_LENGTH, Math.max(NettyNodeIdGenerator.MIN_ID_LENGTH, length));
            this.random = new Random();
        }

        @Override
        public String id() {
            return IntStream.range(0, length)
                    .mapToObj(i -> (char) (42 + random.nextInt(10)))
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        }

    }

}
