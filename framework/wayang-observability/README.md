# Wayang Observability

The `wayang-observability` module defines framework-neutral health, metrics,
telemetry, and tracing contracts. It lets runtimes expose operational state and
instrument agent execution without choosing a telemetry backend.

Register health checks and telemetry services in the host runtime, then bridge
the contracts to the deployment's metrics and tracing system.
