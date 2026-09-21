package tech.kayys.andalus.guardrails.policy;

import tech.kayys.andalus.guardrails.plugin.api.CheckPhase;

public record Policy(
                String id,
                String name,
                String expression,
                String denyMessage,
                PolicySeverity severity,
                CheckPhase phase) {
}