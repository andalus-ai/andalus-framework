package tech.kayys.wayang.execution.runtime;

import java.util.Map;

public record ExecutionStartRequest(
        String executionId,
        String attemptId,
        String targetSandboxId,
        Map<String, Object> environmentVariables,
        Map<String, Object> parameters
) {}
