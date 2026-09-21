package tech.kayys.andalus.guardrails;

import tech.kayys.andalus.guardrails.plugin.api.CheckPhase;
import tech.kayys.andalus.guardrails.policy.PolicySeverity;

public record GuardrailPolicy(
                String id,
                String name,
                String expression,
                String denyMessage,
                PolicySeverity severity,
                CheckPhase phase) {
}
