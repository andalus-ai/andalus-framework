# Wayang Governance

The `wayang-governance` module defines policy assessment and guardrail
contracts. It models evaluators, evaluation status and issues, guardrail
providers and results, violations, and surface-policy preflight and assessment
contexts.

Implement evaluators and guardrail providers in the runtime or a policy module.
Use the assessment contexts to preserve the request surface and relevant
execution information when evaluating a policy.
