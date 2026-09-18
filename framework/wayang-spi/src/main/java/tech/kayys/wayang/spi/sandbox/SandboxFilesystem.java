package tech.kayys.wayang.spi.sandbox;

import java.util.List;

public record SandboxFilesystem(
        boolean readOnlyRoot,
        List<FilesystemMount> mounts
) {

    public SandboxFilesystem {
        mounts = mounts == null
                ? List.of()
                : List.copyOf(mounts);
    }

    public static SandboxFilesystem empty() {
        return new SandboxFilesystem(
                true,
                List.of()
        );
    }
}
