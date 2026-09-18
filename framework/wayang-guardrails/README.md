# Wayang Guardrails

The `wayang-guardrails` module evaluates and enforces safety policies around
agent execution. It includes detector orchestration, PII, toxicity, bias, and
hallucination checks, content redaction, policy repositories, plugin APIs, and
fallback strategies including blocking and human escalation.

Configure a `GuardrailsService` with the policies and detectors appropriate to
the deployment. Use plugin interfaces to add checks without changing the
guardrails engine, and choose an explicit fallback strategy for each policy
outcome.
