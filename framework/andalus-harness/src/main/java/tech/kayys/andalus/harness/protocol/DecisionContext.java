package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.harness.context.ContextSnapshot;
import tech.kayys.andalus.harness.goal.GoalSnapshot;
import tech.kayys.andalus.harness.task.TaskSnapshot;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents the decision context provided to an agent strategy or decision engine.
 *
 * <p>Captures the execution context, turn observations, active goals, active tasks,
 * operational constraints, and execution attributes available to inform an agent's next action.</p>
 */
public record DecisionContext(
        AgentTurnContext turnContext,
        Optional<String> goal,
        Optional<String> task,
        Optional<GoalSnapshot> activeGoal,
        Optional<TaskSnapshot> activeTask,
        DecisionConstraints constraints,
        List<TurnObservation> observations,
        Map<String, Object> attributes
) {

    public DecisionContext {
        Objects.requireNonNull(turnContext, "turnContext cannot be null");
        goal = goal != null ? goal : Optional.empty();
        task = task != null ? task : Optional.empty();
        activeGoal = activeGoal != null ? activeGoal : Optional.empty();
        activeTask = activeTask != null ? activeTask : Optional.empty();

        if (goal.isEmpty() && activeGoal.isPresent()) {
            goal = Optional.of(activeGoal.get().title());
        }
        if (task.isEmpty() && activeTask.isPresent()) {
            task = Optional.of(activeTask.get().title());
        }

        constraints = constraints != null ? constraints : DecisionConstraints.defaults();
        observations = observations != null ? List.copyOf(observations) : List.of();
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public DecisionContext(
            AgentTurnContext turnContext,
            Optional<String> goal,
            Optional<String> task,
            DecisionConstraints constraints,
            List<TurnObservation> observations,
            Map<String, Object> attributes
    ) {
        this(
                turnContext,
                goal,
                task,
                goal.map(g -> GoalSnapshot.of(g, g)),
                task.map(t -> TaskSnapshot.of(t, t)),
                constraints,
                observations,
                attributes
        );
    }

    public DecisionContext(
            AgentTurnContext turnContext,
            List<TurnObservation> observations,
            Map<String, Object> attributes
    ) {
        this(turnContext, Optional.empty(), Optional.empty(), DecisionConstraints.defaults(), observations, attributes);
    }

    public static DecisionContext of(AgentTurnContext turnContext) {
        return new DecisionContext(turnContext, List.of(), Map.of());
    }

    public static DecisionContext of(AgentTurnContext turnContext, List<TurnObservation> observations) {
        return new DecisionContext(turnContext, observations, Map.of());
    }

    public static DecisionContext of(
            AgentTurnContext turnContext,
            String goal,
            String task,
            DecisionConstraints constraints,
            List<TurnObservation> observations
    ) {
        return new DecisionContext(
                turnContext,
                Optional.ofNullable(goal),
                Optional.ofNullable(task),
                constraints,
                observations,
                Map.of()
        );
    }

    public static DecisionContext of(
            AgentTurnContext turnContext,
            GoalSnapshot activeGoal,
            TaskSnapshot activeTask,
            DecisionConstraints constraints,
            List<TurnObservation> observations
    ) {
        return new DecisionContext(
                turnContext,
                activeGoal != null ? Optional.of(activeGoal.title()) : Optional.empty(),
                activeTask != null ? Optional.of(activeTask.title()) : Optional.empty(),
                Optional.ofNullable(activeGoal),
                Optional.ofNullable(activeTask),
                constraints,
                observations,
                Map.of()
        );
    }

    public String executionId() {
        return turnContext.executionId() != null ? turnContext.executionId().value() : null;
    }

    public ContextSnapshot context() {
        return turnContext.context();
    }

    public BudgetView budget() {
        return turnContext.budget();
    }

    public ToolCatalogView tools() {
        return turnContext.tools();
    }

    public CapabilityView capabilities() {
        return turnContext.capabilities();
    }
}
