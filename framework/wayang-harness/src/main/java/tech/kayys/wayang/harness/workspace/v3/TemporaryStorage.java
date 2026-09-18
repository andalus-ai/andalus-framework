package tech.kayys.wayang.harness.workspace.v3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Storage SPI for managing ephemeral scratch spaces that should not pollute permanent artifact storage.
 */
public interface TemporaryStorage {

    TempHandle allocate(String prefix);

    void release(TempHandle handle);

    static TemporaryStorage local() {
        return new LocalTemporaryStorage();
    }
}

class LocalTemporaryStorage implements TemporaryStorage {
    @Override
    public TempHandle allocate(String prefix) {
        String p = prefix != null ? prefix : "wayang-temp";
        try {
            Path dir = Files.createTempDirectory(p);
            return new TempHandle(dir);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to allocate temporary storage", e);
        }
    }

    @Override
    public void release(TempHandle handle) {
        if (handle != null) {
            handle.close();
        }
    }
}
