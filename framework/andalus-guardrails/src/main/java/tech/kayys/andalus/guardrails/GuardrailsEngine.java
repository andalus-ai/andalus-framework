package tech.kayys.andalus.guardrails;

public interface GuardrailsEngine {
    GuardrailResult preCheck(ExecuteNodeTask task);
    GuardrailResult postCheck(ExecuteNodeTask task, ExecutionResult result);
    void registerPolicy(GuardrailPolicy policy);
    void updatePolicy(String policyId, 
        GuardrailPolicy policy);

    default ExecutionResult evaluate(String input) {
        return new ExecutionResult(java.util.Map.of("allowed", true, "input", input), java.util.Map.of());
    }
}
