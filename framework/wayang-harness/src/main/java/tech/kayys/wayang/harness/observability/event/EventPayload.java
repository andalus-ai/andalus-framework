package tech.kayys.wayang.harness.observability.event;

import java.util.Map;
import java.util.Objects;

/**
 * Structured, typed payload of a runtime event.
 */
public interface EventPayload {

    record MapPayload(Map<String, Object> data) implements EventPayload {
        public MapPayload {
            data = data != null ? Map.copyOf(data) : Map.of();
        }

        public static MapPayload of(String key, Object value) {
            return new MapPayload(Map.of(key, value));
        }

        public static MapPayload of(Map<String, Object> data) {
            return new MapPayload(data);
        }
    }

    record ExecutionTransitionPayload(String fromStatus, String toStatus, String reason) implements EventPayload {
        public ExecutionTransitionPayload {
            fromStatus = fromStatus != null ? fromStatus : "UNKNOWN";
            toStatus = Objects.requireNonNull(toStatus, "toStatus");
            reason = reason != null ? reason : "";
        }
    }

    record ToolExecutionPayload(String toolName, boolean success, String summary) implements EventPayload {
        public ToolExecutionPayload {
            Objects.requireNonNull(toolName, "toolName");
            summary = summary != null ? summary : "";
        }
    }

    record ModelInferencePayload(String modelId, int promptTokens, int completionTokens, long latencyMs) implements EventPayload {
        public ModelInferencePayload {
            Objects.requireNonNull(modelId, "modelId");
        }
    }

    static EventPayload empty() {
        return new MapPayload(Map.of());
    }
}
