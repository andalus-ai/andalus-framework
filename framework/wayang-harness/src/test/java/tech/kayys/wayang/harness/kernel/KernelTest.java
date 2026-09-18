package tech.kayys.wayang.harness.kernel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class KernelTest {

    static class RecordingModule implements WayangModule {
        private final ModuleId id;
        private final Set<ModuleDependency> deps;
        private final List<String> events;

        public RecordingModule(String id, Set<ModuleDependency> deps, List<String> events) {
            this.id = ModuleId.of(id);
            this.deps = deps;
            this.events = events;
        }

        @Override
        public ModuleId id() {
            return id;
        }

        @Override
        public ModuleVersion version() {
            return ModuleVersion.of(1, 0, 0);
        }

        @Override
        public ModuleDescriptor descriptor() {
            return ModuleDescriptor.of(id, version(), "Test module " + id.value(), deps);
        }

        @Override
        public void initialize(ModuleContext context) {
            events.add("init:" + id.value());
        }

        @Override
        public void start() {
            events.add("start:" + id.value());
        }

        @Override
        public void stop() {
            events.add("stop:" + id.value());
        }

        @Override
        public void destroy() {
            events.add("destroy:" + id.value());
        }
    }

    @Test
    void testServiceRegistry() {
        ServiceRegistry registry = new DefaultServiceRegistry();
        ServiceKey<String> key = ServiceKey.of(String.class, "greeting");

        registry.register(key, "Hello Wayang");
        assertTrue(registry.contains(key));
        assertEquals("Hello Wayang", registry.require(key));
    }

    @Test
    void testModuleLifecycleAndTopologicalSort() {
        List<String> events = new ArrayList<>();

        // Module A depends on B
        WayangModule moduleB = new RecordingModule("module-b", Set.of(), events);
        WayangModule moduleA = new RecordingModule(
                "module-a",
                Set.of(ModuleDependency.required(ModuleId.of("module-b"))),
                events
        );

        HarnessKernel kernel = WayangCompositionRoot.create()
                .withId(HarnessId.of("test-harness"))
                .registerModule(moduleA)
                .registerModule(moduleB)
                .build();

        assertEquals(HarnessState.CREATED, kernel.state());

        kernel.initialize();
        assertEquals(HarnessState.INITIALIZED, kernel.state());

        kernel.start();
        assertEquals(HarnessState.RUNNING, kernel.state());

        // B must have started before A
        int startB = events.indexOf("start:module-b");
        int startA = events.indexOf("start:module-a");
        assertTrue(startB >= 0 && startA >= 0);
        assertTrue(startB < startA, "Module B should start before Module A");

        kernel.stop();
        assertEquals(HarnessState.STOPPED, kernel.state());

        // A must stop before B (reverse order)
        int stopA = events.indexOf("stop:module-a");
        int stopB = events.indexOf("stop:module-b");
        assertTrue(stopA >= 0 && stopB >= 0);
        assertTrue(stopA < stopB, "Module A should stop before Module B");
    }

    @Test
    void testCircularDependencyDetection() {
        List<String> events = new ArrayList<>();

        // A depends on B, B depends on A
        WayangModule moduleA = new RecordingModule(
                "mod-a",
                Set.of(ModuleDependency.required(ModuleId.of("mod-b"))),
                events
        );
        WayangModule moduleB = new RecordingModule(
                "mod-b",
                Set.of(ModuleDependency.required(ModuleId.of("mod-a"))),
                events
        );

        HarnessKernel kernel = WayangCompositionRoot.create()
                .registerModule(moduleA)
                .registerModule(moduleB)
                .build();

        assertThrows(RuntimeException.class, kernel::initialize);
        assertEquals(HarnessState.FAILED, kernel.state());
    }
}
