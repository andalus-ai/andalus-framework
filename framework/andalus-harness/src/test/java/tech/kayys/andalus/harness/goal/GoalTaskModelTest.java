package tech.kayys.andalus.harness.goal;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.context.ContextId;
import tech.kayys.andalus.harness.context.ContextSnapshot;
import tech.kayys.andalus.harness.context.DefaultAssembledContext;
import tech.kayys.andalus.harness.context.DefaultHarnessIdentity;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.protocol.AgentTask;
import tech.kayys.andalus.harness.protocol.AgentTurnContext;
import tech.kayys.andalus.harness.protocol.DecisionConstraints;
import tech.kayys.andalus.harness.protocol.DecisionContext;
import tech.kayys.andalus.harness.task.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class GoalTaskModelTest {

    @Test
    void goalDefinitionAndLifecycleTransitions() {
        GoalCriterion c1 = GoalCriterion.of("crit-1", "Authentication tests must pass");
        GoalDefinition def = new GoalDefinition(
                GoalId.generate(),
                "Fix auth bug",
                "Ensure login succeeds",
                "Working authentication",
                Optional.empty(),
                List.of(c1),
                Map.of("category", "security")
        );

        assertThat(def.title()).isEqualTo("Fix auth bug");
        assertThat(def.successCriteria()).hasSize(1);

        GoalInstance instance = new GoalInstance(
                def.id(),
                def,
                "exec-123",
                Optional.empty(),
                GoalState.PROPOSED,
                Instant.now(),
                0L,
                Map.of()
        );

        assertThat(instance.state()).isEqualTo(GoalState.PROPOSED);
        assertThat(instance.revision()).isEqualTo(0L);

        // Transition to ACTIVE
        GoalInstance active = instance.transitionTo(GoalState.ACTIVE);
        assertThat(active.state()).isEqualTo(GoalState.ACTIVE);
        assertThat(active.revision()).isEqualTo(1L);

        // Evaluate satisfaction
        GoalEvaluation eval = GoalEvaluation.satisfied("All tests green");
        assertThat(eval.criteriaSatisfied()).isTrue();
        assertThat(eval.proposedState()).isEqualTo(GoalState.SATISFIED);

        GoalInstance satisfied = active.transitionTo(eval.proposedState());
        assertThat(satisfied.state()).isEqualTo(GoalState.SATISFIED);
        assertThat(satisfied.state().isTerminal()).isTrue();
        assertThat(satisfied.revision()).isEqualTo(2L);
    }

    @Test
    void taskDefinitionReadinessAndCompletion() {
        TaskId t1 = TaskId.generate();
        TaskId t2 = TaskId.generate();

        TaskDefinition depTask = TaskDefinition.of("Inspect code", "Locate regression");
        TaskDefinition mainTask = new TaskDefinition(
                t2,
                "Fix regression",
                "Apply patch",
                Optional.empty(),
                List.of(t1),
                Map.of("patch_type", "null_check"),
                Map.of()
        );

        // Readiness when dependency is unmet
        TaskReadiness blocked = TaskReadiness.blocked(List.of(t1), "Prerequisite task not completed");
        assertThat(blocked.ready()).isFalse();
        assertThat(blocked.unmetDependencies()).contains(t1);

        // When dependency satisfies
        TaskReadiness ready = TaskReadiness.ofReady();
        assertThat(ready.ready()).isTrue();
        assertThat(ready.unmetDependencies()).isEmpty();

        // Lifecycle transitions
        TaskInstance taskInstance = new TaskInstance(
                t2,
                mainTask,
                "exec-123",
                Optional.empty(),
                Optional.empty(),
                TaskState.READY,
                0L,
                Optional.empty(),
                Instant.now(),
                Instant.now(),
                Map.of()
        );

        TaskInstance running = taskInstance.transitionTo(TaskState.RUNNING);
        assertThat(running.state()).isEqualTo(TaskState.RUNNING);
        assertThat(running.revision()).isEqualTo(1L);

        TaskResult result = TaskResult.success(t2, "Patch applied successfully");
        TaskInstance completed = running.complete(result);
        assertThat(completed.state()).isEqualTo(TaskState.COMPLETED);
        assertThat(completed.state().isTerminal()).isTrue();
        assertThat(completed.result()).isPresent();
        assertThat(completed.result().get().summary()).contains("Patch applied successfully");
        assertThat(completed.revision()).isEqualTo(2L);
    }

    @Test
    void agentTaskBidirectionalCompatibility() {
        AgentTask agentTask = new AgentTask("Execute sql migration", Map.of("timeout", 5000));
        TaskDefinition taskDef = TaskDefinition.fromAgentTask(agentTask);

        assertThat(taskDef.title()).isEqualTo("Execute sql migration");
        assertThat(taskDef.description()).isEqualTo("Execute sql migration");
        assertThat(taskDef.input()).containsEntry("timeout", 5000);

        AgentTask roundTrip = taskDef.toAgentTask();
        assertThat(roundTrip.description()).isEqualTo(agentTask.description());
        assertThat(roundTrip.input()).isEqualTo(agentTask.input());
    }

    @Test
    void goalAndTaskSnapshotsInDecisionContext() {
        GoalSnapshot goalSnapshot = GoalSnapshot.of("Deliver report", "Economic analysis delivered");
        TaskSnapshot taskSnapshot = TaskSnapshot.of("Retrieve GDP data", "Query national bank API");

        AgentTurnContext mockTurnContext = new AgentTurnContext() {
            @Override
            public ExecutionId executionId() {
                return ExecutionId.generate();
            }

            @Override
            public DefaultHarnessIdentity identity() {
                return DefaultHarnessIdentity.of("agent-test");
            }

            @Override
            public ContextSnapshot context() {
                return new ContextSnapshot(
                        "snap-1",
                        Instant.now(),
                        new DefaultAssembledContext(ContextId.generate(), List.of(), Map.of(), 0L),
                        Map.of()
                );
            }

            @Override
            public tech.kayys.andalus.harness.protocol.CapabilityView capabilities() { return null; }
            @Override
            public tech.kayys.andalus.harness.protocol.BudgetView budget() { return null; }
            @Override
            public tech.kayys.andalus.harness.protocol.ToolCatalogView tools() { return null; }
            @Override
            public tech.kayys.andalus.harness.protocol.ModelCatalogView models() { return null; }
        };

        DecisionContext dc = DecisionContext.of(
                mockTurnContext,
                goalSnapshot,
                taskSnapshot,
                DecisionConstraints.defaults(),
                List.of()
        );

        assertThat(dc.activeGoal()).contains(goalSnapshot);
        assertThat(dc.activeTask()).contains(taskSnapshot);
        assertThat(dc.goal()).contains("Deliver report");
        assertThat(dc.task()).contains("Retrieve GDP data");
    }
}
