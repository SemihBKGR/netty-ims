package com.semihbkgr.nettyims;

import lombok.NonNull;

import java.util.Random;

public interface UsernameGenerator {

    UsernameGenerator randomUsernameGenerator = new RandomUsernameGenerator("user", "", 9);

    String username();

    static String randomUsername() {
        return randomUsernameGenerator.username();
    }

    class RandomUsernameGenerator implements UsernameGenerator {

        private final Random random;
        private final String root;
        private final int idLength;

        private RandomUsernameGenerator(@NonNull String root, int idLength) {
            this.random = new Random();
            this.root = root;
            if (idLength < 5) {
                idLength = 5;
            }
            this.idLength = idLength;
        }

        private RandomUsernameGenerator(@NonNull String base, @NonNull String root, int idLength) {
            this(base + root, idLength);
        }

        @Override
        public String username() {
            var usernameBuilder = new StringBuilder(root);
            for (int i = 0; i < idLength; i++) {
                usernameBuilder.append((char) (random.nextInt(10) + 48));
            }
            return usernameBuilder.toString();
        }

    }

}
