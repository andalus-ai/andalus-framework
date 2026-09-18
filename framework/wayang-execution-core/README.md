# Wayang Execution Core

The `wayang-execution-core` module provides the default implementation of the
Wayang execution API. It includes invocation and invocation-chain objects,
execution context and ID generation, cancellation tokens, interceptor
composition, and the default execution pipeline.

Use `DefaultExecutionPipeline` as the standard pipeline and add
`AgentInvocationInterceptor` implementations to apply cross-cutting behavior
such as tracing, policy checks, or metrics.
