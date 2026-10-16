package tech.kayys.andalus.skill.lifecycle;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.skill.spi.SkillReference;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SkillLifecycleTest {

    @Test
    void lifecycleManagerActivationAndDeactivation() throws Exception {
        InMemorySkillLifecycleManager manager = new InMemorySkillLifecycleManager();
        SkillReference ref = SkillReference.of("skill-analyser");

        // Initially discovered
        assertThat(manager.currentState(ref)).isEqualTo(SkillLifecycleState.DISCOVERED);

        // Activate
        SkillLifecycleState activated = manager.activate(ref).toCompletableFuture().get();
        assertThat(activated).isEqualTo(SkillLifecycleState.ACTIVE);
        assertThat(manager.currentState(ref)).isEqualTo(SkillLifecycleState.ACTIVE);

        // Deactivate
        SkillLifecycleState deactivated = manager.deactivate(ref).toCompletableFuture().get();
        assertThat(deactivated).isEqualTo(SkillLifecycleState.DISABLED);
        assertThat(manager.currentState(ref)).isEqualTo(SkillLifecycleState.DISABLED);

        // Reactivate
        SkillLifecycleState reactivated = manager.activate(ref).toCompletableFuture().get();
        assertThat(reactivated).isEqualTo(SkillLifecycleState.ACTIVE);
    }

    @Test
    void lifecycleManagerUninstall() throws Exception {
        InMemorySkillLifecycleManager manager = new InMemorySkillLifecycleManager();
        SkillReference ref = SkillReference.of("skill-temp");

        manager.activate(ref).toCompletableFuture().get();
        manager.uninstall(ref).toCompletableFuture().get();

        assertThat(manager.currentState(ref)).isEqualTo(SkillLifecycleState.UNINSTALLED);
    }

    @Test
    void lifecycleEventListenerNotification() throws Exception {
        InMemorySkillLifecycleManager manager = new InMemorySkillLifecycleManager();
        SkillReference ref = SkillReference.of("skill-monitored");

        List<SkillLifecycleEvent> capturedEvents = new ArrayList<>();
        SkillLifecycleListener listener = capturedEvents::add;
        manager.addListener(listener);

        manager.activate(ref).toCompletableFuture().get();
        manager.deactivate(ref).toCompletableFuture().get();

        assertThat(capturedEvents).hasSize(2);

        SkillLifecycleEvent ev1 = capturedEvents.get(0);
        assertThat(ev1.skill()).isEqualTo(ref);
        assertThat(ev1.previousState()).isNull(); // first put returns null
        assertThat(ev1.newState()).isEqualTo(SkillLifecycleState.ACTIVE);
        assertThat(ev1.reason()).isEqualTo("activated");

        SkillLifecycleEvent ev2 = capturedEvents.get(1);
        assertThat(ev2.previousState()).isEqualTo(SkillLifecycleState.ACTIVE);
        assertThat(ev2.newState()).isEqualTo(SkillLifecycleState.DISABLED);
        assertThat(ev2.reason()).isEqualTo("deactivated");

        // Remove listener
        manager.removeListener(listener);
        manager.activate(ref).toCompletableFuture().get();
        assertThat(capturedEvents).hasSize(2); // no new events captured
    }

    @Test
    void lifecycleEventFactory() {
        SkillReference ref = SkillReference.of("skill-x");
        SkillLifecycleEvent ev = SkillLifecycleEvent.of(ref, SkillLifecycleState.DISCOVERED, SkillLifecycleState.VALIDATED);

        assertThat(ev.skill()).isEqualTo(ref);
        assertThat(ev.previousState()).isEqualTo(SkillLifecycleState.DISCOVERED);
        assertThat(ev.newState()).isEqualTo(SkillLifecycleState.VALIDATED);
        assertThat(ev.occurredAt()).isNotNull();
        assertThat(ev.reason()).isEmpty();
    }
}
