package tech.kayys.wayang.spi.sandbox;

import java.nio.file.Path;
import java.util.Objects;

public record FilesystemRoot(
        String id,
        Path path,
        FilesystemAccess access,
        boolean followSymlinks
) {

    public FilesystemRoot {
        id = Objects.requireNonNull(id, "id");
        path = Objects.requireNonNull(path, "path");
        access = Objects.requireNonNull(access, "access");
    }

    public boolean readable() {
        return access == FilesystemAccess.READ
                || access == FilesystemAccess.READ_WRITE;
    }

    public boolean writable() {
        return access == FilesystemAccess.WRITE
                || access == FilesystemAccess.READ_WRITE;
    }
}
