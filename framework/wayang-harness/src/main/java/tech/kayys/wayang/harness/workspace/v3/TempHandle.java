package tech.kayys.wayang.harness.workspace.v3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Handle to temporary scratch storage allocated for an execution.
 */
public record TempHandle(Path path) implements AutoCloseable {
    public TempHandle {
        Objects.requireNonNull(path, "path");
    }

    @Override
    public void close() {
        if (Files.exists(path)) {
            try (Stream<Path> stream = Files.walk(path)) {
                stream.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {}
                });
            } catch (IOException ignored) {}
        }
    }
}
