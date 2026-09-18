package tech.kayys.wayang.spi.sandbox;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface SandboxFilesystem {

    ResolvedSandboxPath resolve(
            String rootId,
            Path requested);

    Optional<FilesystemRoot> root(
            String rootId);

    boolean exists(
            String rootId,
            Path requested);

    void validateRead(
            String rootId,
            Path requested);

    void validateWrite(
            String rootId,
            Path requested);

    default boolean readOnlyRoot() {
        return true;
    }

    default List<FilesystemMount> mounts() {
        return List.of();
    }

    static SandboxFilesystem empty() {
        return new SandboxFilesystem() {
            @Override
            public ResolvedSandboxPath resolve(String rootId, Path requested) {
                throw new UnsupportedOperationException("Empty filesystem cannot resolve paths");
            }

            @Override
            public Optional<FilesystemRoot> root(String rootId) {
                return Optional.empty();
            }

            @Override
            public boolean exists(String rootId, Path requested) {
                return false;
            }

            @Override
            public void validateRead(String rootId, Path requested) {
                throw new UnsupportedOperationException("Empty filesystem cannot read");
            }

            @Override
            public void validateWrite(String rootId, Path requested) {
                throw new UnsupportedOperationException("Empty filesystem cannot write");
            }
        };
    }
}
