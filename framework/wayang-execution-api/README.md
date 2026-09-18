# Wayang Execution API

The `wayang-execution-api` module defines the contracts for invoking agents
through an execution pipeline. It covers invocation handlers and interceptors,
execution context and metadata, lifecycle notifications, results, errors, and
cancellation tokens.

Applications compose `AgentInvocationInterceptor` instances around an
`ExecutionPipeline`. Depend on this module when callers need execution
semantics without depending on the default implementation.
