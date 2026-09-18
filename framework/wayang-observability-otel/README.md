# Wayang OpenTelemetry

The `wayang-observability-otel` module connects Wayang telemetry to
OpenTelemetry. It provides the OpenTelemetry listener and integration types
needed to export agent lifecycle and execution signals.

Configure the OpenTelemetry SDK in the host runtime, then register the Wayang
listener with the observability services.
