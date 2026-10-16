package tech.kayys.andalus.guardrails;

import java.util.Map;

public record ExecutionResult(
        Map<String, Object> outputs,
        Map<String, Object> metadata) {

    public boolean isAllowed() {
        return outputs == null || !Boolean.FALSE.equals(outputs.get("allowed"));
    }
}
