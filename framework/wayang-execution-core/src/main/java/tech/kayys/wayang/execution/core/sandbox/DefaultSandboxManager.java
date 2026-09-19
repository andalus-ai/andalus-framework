package tech.kayys.wayang.execution.core.sandbox;

import tech.kayys.wayang.execution.sandbox.SandboxHandle;
import tech.kayys.wayang.execution.sandbox.SandboxId;
import tech.kayys.wayang.execution.sandbox.SandboxManager;
import tech.kayys.wayang.execution.sandbox.SandboxRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Standard implementation of {@link SandboxManager}.
 */
public class DefaultSandboxManager implements SandboxManager {

    private final Map<SandboxId, SandboxHandle> sandboxes = new ConcurrentHashMap<>();

    @Override
    public SandboxHandle create(SandboxRequest request) {
        Objects.requireNonNull(request, "SandboxRequest cannot be null");
        SandboxHandle handle = new DefaultSandboxHandle(request.sandboxId(), request.specification());
        sandboxes.put(handle.id(), handle);
        return handle;
    }

    @Override
    public SandboxHandle start(SandboxId sandboxId) {
        SandboxHandle handle = sandboxes.get(sandboxId);
        if (handle == null) {
            throw new IllegalArgumentException("Unknown sandbox: " + sandboxId.value());
        }
        try {
            handle.start();
            return handle;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start sandbox: " + sandboxId.value(), e);
        }
    }

    @Override
    public void pause(SandboxId sandboxId) {
        SandboxHandle handle = sandboxes.get(sandboxId);
        if (handle != null) {
            try {
                handle.pause();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to pause sandbox: " + sandboxId.value(), e);
            }
        }
    }

    @Override
    public void destroy(SandboxId sandboxId) {
        SandboxHandle handle = sandboxes.remove(sandboxId);
        if (handle != null) {
            try {
                handle.destroy();
            } catch (Exception e) {
                throw new IllegalStateException("Failed to destroy sandbox: " + sandboxId.value(), e);
            }
        }
    }

    @Override
    public Optional<SandboxHandle> find(SandboxId sandboxId) {
        return Optional.ofNullable(sandboxes.get(sandboxId));
    }

    @Override
    public List<SandboxHandle> list() {
        return List.copyOf(sandboxes.values());
    }
}
