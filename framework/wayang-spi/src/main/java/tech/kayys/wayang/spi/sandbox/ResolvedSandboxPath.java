package tech.kayys.wayang.spi.sandbox;

import java.nio.file.Path;
import java.util.Objects;

public record ResolvedSandboxPath(
        String rootId,
        Path path,
        FilesystemAccess access
) {
    public ResolvedSandboxPath {
        rootId = Objects.requireNonNull(rootId, "rootId");
        path = Objects.requireNonNull(path, "path");
        access = Objects.requireNonNull(access, "access");
    }
}
