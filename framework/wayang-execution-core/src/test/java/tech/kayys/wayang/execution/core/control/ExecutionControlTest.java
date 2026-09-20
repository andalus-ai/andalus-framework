package tech.kayys.wayang.execution.core.control;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.kayys.wayang.execution.control.CommandSource;
import tech.kayys.wayang.execution.control.ControlAction;
import tech.kayys.wayang.execution.control.ControlCommand;
import tech.kayys.wayang.execution.control.ControlEvent;
import tech.kayys.wayang.execution.control.ControlResult;
import tech.kayys.wayang.execution.control.ExecutionLifecycleState;
import tech.kayys.wayang.execution.control.ExecutionPriority;
import tech.kayys.wayang.execution.control.HealthSnapshot;
import tech.kayys.wayang.execution.control.HealthStatus;
import tech.kayys.wayang.execution.control.SchedulingDecision;
import tech.kayys.wayang.execution.control.SupervisionContext;
import tech.kayys.wayang.execution.control.SupervisionDecision;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionControlTest {

    private DefaultExecutionTransitionPolicy transitionPolicy;
    private DefaultExecutionController controller;
    private DefaultExecutionSupervisor supervisor;
    private DefaultExecutionHealthMonitor healthMonitor;
    private DefaultExecutionScheduler scheduler;
    private DefaultWayangControl wayangControl;

    @BeforeEach
    void setUp() {
        transitionPolicy = new DefaultExecutionTransitionPolicy();
        controller = new DefaultExecutionController(transitionPolicy);
        supervisor = new DefaultExecutionSupervisor(3);
        healthMonitor = new DefaultExecutionHealthMonitor(controller);
        scheduler = new DefaultExecutionScheduler("cloud-worker-1", 50);
        wayangControl = new DefaultWayangControl(controller, supervisor, healthMonitor, scheduler);
    }

    @Test
    @DisplayName("ExecutionTransitionPolicy permits valid lifecycle transitions and rejects invalid ones")
    void testTransitionPolicy() {
        // CREATED -> START -> RUNNING
        var d1 = transitionPolicy.evaluate(ExecutionLifecycleState.CREATED, ControlAction.START, null);
        assertTrue(d1.allowed());
        assertEquals(ExecutionLifecycleState.RUNNING, d1.targetState());

        // RUNNING -> PAUSE -> PAUSED
        var d2 = transitionPolicy.evaluate(ExecutionLifecycleState.RUNNING, ControlAction.PAUSE, null);
        assertTrue(d2.allowed());
        assertEquals(ExecutionLifecycleState.PAUSED, d2.targetState());

        // PAUSED -> RESUME -> RUNNING
        var d3 = transitionPolicy.evaluate(ExecutionLifecycleState.PAUSED, ControlAction.RESUME, null);
        assertTrue(d3.allowed());
        assertEquals(ExecutionLifecycleState.RUNNING, d3.targetState());

        // PAUSED -> PAUSE -> rejected
        var dInvalid = transitionPolicy.evaluate(ExecutionLifecycleState.PAUSED, ControlAction.PAUSE, null);
        assertFalse(dInvalid.allowed());

        // COMPLETED -> RESUME -> rejected
        var dTerminal = transitionPolicy.evaluate(ExecutionLifecycleState.COMPLETED, ControlAction.RESUME, null);
        assertFalse(dTerminal.allowed());

        // RUNNING -> CANCEL -> CANCELLED
        var dCancel = transitionPolicy.evaluate(ExecutionLifecycleState.RUNNING, ControlAction.CANCEL, null);
        assertTrue(dCancel.allowed());
        assertEquals(ExecutionLifecycleState.CANCELLED, dCancel.targetState());
    }

    @Test
    @DisplayName("DefaultExecutionController translates commands and records audit events")
    void testExecutionControllerAndEvents() {
        String execId = "ctrl-exec-1";
        controller.registerExecution(execId, ExecutionLifecycleState.CREATED);

        ControlResult res1 = controller.apply(execId, ControlCommand.of(execId, ControlAction.START, CommandSource.USER));
        assertTrue(res1.success());
        assertEquals(ExecutionLifecycleState.RUNNING, controller.getState(execId).orElseThrow());

        ControlResult res2 = controller.apply(execId, ControlCommand.of(execId, ControlAction.PAUSE, CommandSource.SUPERVISOR));
        assertTrue(res2.success());
        assertEquals(ExecutionLifecycleState.PAUSED, controller.getState(execId).orElseThrow());

        List<ControlEvent> events = controller.getEvents(execId);
        assertEquals(2, events.size());
        assertEquals(ExecutionLifecycleState.CREATED, events.get(0).previous());
        assertEquals(ExecutionLifecycleState.RUNNING, events.get(0).current());
        assertEquals(ExecutionLifecycleState.RUNNING, events.get(1).previous());
        assertEquals(ExecutionLifecycleState.PAUSED, events.get(1).current());
    }

    @Test
    @DisplayName("DefaultExecutionSupervisor manages retries and quarantine limits")
    void testExecutionSupervisor() {
        // Healthy running execution continues
        SupervisionContext ctxRunning = SupervisionContext.of("exec-1", ExecutionLifecycleState.RUNNING);
        SupervisionDecision decRunning = supervisor.supervise(ctxRunning);
        assertEquals(ControlAction.CONTINUE, decRunning.action());

        // First failure triggers retry with backoff
        SupervisionContext ctxFail1 = new SupervisionContext("exec-1", ExecutionLifecycleState.FAILED, 0, 1000L, null);
        SupervisionDecision decFail1 = supervisor.supervise(ctxFail1);
        assertEquals(ControlAction.RETRY, decFail1.action());
        assertFalse(decFail1.delay().isZero());

        // 3 consecutive failures exceeded triggers quarantine
        SupervisionContext ctxExceeded = new SupervisionContext("exec-1", ExecutionLifecycleState.FAILED, 3, 5000L, null);
        SupervisionDecision decQuarantine = supervisor.supervise(ctxExceeded);
        assertEquals(ControlAction.QUARANTINE, decQuarantine.action());
    }

    @Test
    @DisplayName("DefaultExecutionHealthMonitor reports health based on lifecycle states")
    void testExecutionHealthMonitor() {
        String execId = "health-exec-1";
        controller.registerExecution(execId, ExecutionLifecycleState.RUNNING);

        HealthSnapshot snap1 = healthMonitor.inspect(execId);
        assertEquals(HealthStatus.HEALTHY, snap1.status());

        controller.apply(execId, ControlCommand.of(execId, ControlAction.CANCEL, CommandSource.USER));
        HealthSnapshot snap2 = healthMonitor.inspect(execId);
        assertEquals(HealthStatus.DEAD, snap2.status());
    }

    @Test
    @DisplayName("DefaultExecutionScheduler handles priority and admission")
    void testExecutionScheduler() {
        SchedulingDecision normal = scheduler.schedule("exec-sch-1", ExecutionPriority.NORMAL);
        assertTrue(normal.admitted());
        assertEquals("cloud-worker-1", normal.targetRuntimeId());

        SchedulingDecision critical = scheduler.schedule("exec-sch-2", ExecutionPriority.CRITICAL);
        assertTrue(critical.admitted());
        assertEquals(ExecutionPriority.CRITICAL, critical.priority());
    }

    @Test
    @DisplayName("DefaultWayangControl facade coordinates execution lifecycle")
    void testWayangControlFacade() {
        String execId = "facade-exec-1";
        controller.registerExecution(execId, ExecutionLifecycleState.RUNNING);

        ControlResult rPause = wayangControl.pause(execId);
        assertTrue(rPause.success());
        assertEquals(ExecutionLifecycleState.PAUSED, wayangControl.getState(execId).orElseThrow());

        ControlResult rResume = wayangControl.resume(execId);
        assertTrue(rResume.success());
        assertEquals(ExecutionLifecycleState.RUNNING, wayangControl.getState(execId).orElseThrow());

        ControlResult rCp = wayangControl.checkpoint(execId);
        assertTrue(rCp.success());
        assertEquals(ExecutionLifecycleState.CHECKPOINTED, wayangControl.getState(execId).orElseThrow());

        ControlResult rMig = wayangControl.migrate(execId, "target-cloud");
        assertTrue(rMig.success());
        assertEquals(ExecutionLifecycleState.MIGRATING, wayangControl.getState(execId).orElseThrow());

        ControlResult rResumeAfterMig = wayangControl.resume(execId);
        assertTrue(rResumeAfterMig.success());
        assertEquals(ExecutionLifecycleState.RUNNING, wayangControl.getState(execId).orElseThrow());

        HealthSnapshot health = wayangControl.inspectHealth(execId);
        assertEquals(HealthStatus.HEALTHY, health.status());

        SupervisionDecision sup = wayangControl.supervise(execId);
        assertEquals(ControlAction.CONTINUE, sup.action());

        ControlResult rCancel = wayangControl.cancel(execId, "user cancelled");
        assertTrue(rCancel.success());
        assertEquals(ExecutionLifecycleState.CANCELLED, wayangControl.getState(execId).orElseThrow());
    }
}
