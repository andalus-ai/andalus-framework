package tech.kayys.wayang.spi.sandbox.container;

import java.util.Objects;

public record ContainerUser(
        String username,
        int uid,
        int gid
) {

    public ContainerUser {
        username = Objects.requireNonNull(username, "username");
        if (uid < 0) {
            throw new IllegalArgumentException("uid must be >= 0");
        }

        if (gid < 0) {
            throw new IllegalArgumentException("gid must be >= 0");
        }
    }

    public boolean isRoot() {
        return uid == 0;
    }

    public static ContainerUser nonRoot(int uid, int gid) {
        if (uid == 0) {
            throw new IllegalArgumentException("non-root uid required");
        }

        return new ContainerUser("wayang", uid, gid);
    }
}
