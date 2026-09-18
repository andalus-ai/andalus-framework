package tech.kayys.wayang.spi.sandbox;

import java.util.List;

public record FilesystemSandboxPolicy(
        List<FilesystemRoot> roots,
        boolean denyAbsolutePaths,
        boolean denyParentTraversal,
        boolean denySymlinkEscape
) {

    public FilesystemSandboxPolicy {
        roots = roots == null
                ? List.of()
                : List.copyOf(roots);
    }

    public static FilesystemSandboxPolicy strict(
            List<FilesystemRoot> roots) {

        return new FilesystemSandboxPolicy(
                roots,
                true,
                true,
                true
        );
    }
}
