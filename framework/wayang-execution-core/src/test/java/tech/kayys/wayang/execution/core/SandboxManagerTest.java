package tech.kayys.wayang.execution.core;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.core.sandbox.DefaultSandboxManager;
import tech.kayys.wayang.execution.sandbox.*;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class SandboxManagerTest {

    @Test
    void testSandboxLifecycleTransitions() throws Exception {
        DefaultSandboxManager manager = new DefaultSandboxManager();
        SandboxId id = SandboxId.of("sbx-lifecycle-test");

        AtomicBoolean started = new AtomicBoolean(false);
        AtomicBoolean destroyed = new AtomicBoolean(false);

        SandboxLifecycleHook hook = new SandboxLifecycleHook() {
            @Override
            public void afterStart(ExecutionSandbox sandbox) {
                started.set(true);
            }

            @Override
            public void afterDestroy(SandboxId destroyedId) {
                destroyed.set(true);
            }
        };

        SandboxSpec spec = new SandboxSpec(
                null,
                null,
                null,
                null,
                null,
                null,
                new SandboxLifecycle(null, null, true, java.util.List.of(hook))
        );

        SandboxHandle handle = manager.create(new SandboxRequest(id, spec, java.util.Map.of()));
        assertNotNull(handle);
        assertEquals(SandboxState.READY, handle.state());

        handle.start();
        assertEquals(SandboxState.RUNNING, handle.state());
        assertTrue(started.get());

        handle.pause();
        assertEquals(SandboxState.PAUSED, handle.state());

        handle.destroy();
        assertEquals(SandboxState.DESTROYED, handle.state());
        assertTrue(destroyed.get());
    }
}
