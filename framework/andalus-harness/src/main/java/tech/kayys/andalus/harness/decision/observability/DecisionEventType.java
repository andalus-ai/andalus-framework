package tech.kayys.andalus.harness.decision.observability;

import tech.kayys.andalus.harness.observability.event.EventDomain;
import tech.kayys.andalus.harness.observability.event.EventType;

/**
 * Canonical decision lifecycle event types for Phase 4N Decision Observability.
 *
 * <p>All constants are {@link EventType} instances using the {@code DECISION} domain,
 * so they integrate naturally with the existing {@code EventBus} and {@code EventJournal}
 * without introducing a second event infrastructure.
 *
 * <p>Naming convention: {@code andalus.decision.<action>}
 */
public final class DecisionEventType {

    private DecisionEventType() {}

    /** A decision attempt began. */
    public static final EventType STARTED =
            EventType.of(EventDomain.DECISION, "started");

    /** The decision context was assembled from immutable snapshots. */
    public static final EventType CONTEXT_READY =
            EventType.of(EventDomain.DECISION, "context_ready");

    /** A strategy was selected for the current turn. */
    public static final EventType STRATEGY_SELECTED =
            EventType.of(EventDomain.DECISION, "strategy_selected");

    /** Planning produced a result (feasible or infeasible). */
    public static final EventType PLANNING_COMPLETED =
            EventType.of(EventDomain.DECISION, "planning_completed");

    /** A reasoning operation completed. */
    public static final EventType REASONING_COMPLETED =
            EventType.of(EventDomain.DECISION, "reasoning_completed");

    /** Candidate actions were discovered during selection. */
    public static final EventType CANDIDATES_DISCOVERED =
            EventType.of(EventDomain.DECISION, "candidates_discovered");

    /** A candidate action was excluded, with a recorded reason. */
    public static final EventType CANDIDATE_EXCLUDED =
            EventType.of(EventDomain.DECISION, "candidate_excluded");

    /** The decision engine produced a proposed action. */
    public static final EventType PRODUCED =
            EventType.of(EventDomain.DECISION, "produced");

    /** Structural and runtime validation of the decision completed. */
    public static final EventType VALIDATED =
            EventType.of(EventDomain.DECISION, "validated");

    /** Governance returned a result (ALLOW, DENY, WAIT, etc.). */
    public static final EventType GOVERNANCE_COMPLETED =
            EventType.of(EventDomain.DECISION, "governance_completed");

    /** The action is waiting for an external condition (e.g. human approval). */
    public static final EventType WAITING =
            EventType.of(EventDomain.DECISION, "waiting");

    /** The action was handed to the execution runtime for dispatch. */
    public static final EventType DISPATCHED =
            EventType.of(EventDomain.DECISION, "dispatched");

    /** An execution result was received (success, failure, or timeout). */
    public static final EventType OUTCOME_RECEIVED =
            EventType.of(EventDomain.DECISION, "outcome_received");

    /** The decision lifecycle reached its defined terminal state. */
    public static final EventType COMPLETED =
            EventType.of(EventDomain.DECISION, "completed");

    /** The decision operation failed before producing a result. */
    public static final EventType FAILED =
            EventType.of(EventDomain.DECISION, "failed");

    /** An operation exceeded its configured deadline. */
    public static final EventType TIMED_OUT =
            EventType.of(EventDomain.DECISION, "timed_out");

    /** The operation was cancelled. */
    public static final EventType CANCELLED =
            EventType.of(EventDomain.DECISION, "cancelled");

    /** An explicit replanning request was issued. */
    public static final EventType REPLANNING_REQUESTED =
            EventType.of(EventDomain.DECISION, "replanning_requested");
}
