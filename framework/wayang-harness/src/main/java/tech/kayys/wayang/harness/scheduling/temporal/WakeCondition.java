package tech.kayys.wayang.harness.scheduling.temporal;

import tech.kayys.wayang.harness.execution.state.ExecutionId;
import tech.kayys.wayang.harness.observability.event.EventFilter;

import java.time.Instant;
import java.util.Objects;

/**
 * Sealed specification declaring why an execution is waiting and what can wake it.
 */
public sealed interface WakeCondition
        permits WakeCondition.TimeWake,
                WakeCondition.EventWake,
                WakeCondition.HumanWake,
                WakeCondition.ExecutionWake,
                WakeCondition.ConditionWake {

    record TimeWake(Instant wakeAt) implements WakeCondition {
        public TimeWake {
            Objects.requireNonNull(wakeAt, "wakeAt");
        }
    }

    record EventWake(EventFilter filter) implements WakeCondition {
        public EventWake {
            Objects.requireNonNull(filter, "filter");
        }
    }

    record HumanWake(String approvalId) implements WakeCondition {
        public HumanWake {
            Objects.requireNonNull(approvalId, "approvalId");
        }
    }

    record ExecutionWake(ExecutionId targetExecutionId) implements WakeCondition {
        public ExecutionWake {
            Objects.requireNonNull(targetExecutionId, "targetExecutionId");
        }
    }

    record ConditionWake(String conditionExpression) implements WakeCondition {
        public ConditionWake {
            Objects.requireNonNull(conditionExpression, "conditionExpression");
        }
    }
}
