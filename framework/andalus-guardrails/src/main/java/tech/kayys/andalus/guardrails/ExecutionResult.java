package tech.kayys.andalus.guardrails;

import java.util.Map;

public record ExecutionResult(
        Map<String, Object> outputs,
        Map<String, Object> metadata) {
}
