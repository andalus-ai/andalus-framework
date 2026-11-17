# Andalus Agent

The `andalus-agent` module provides reusable agent implementations and the
agent-facing orchestration API. It includes ReAct, Plan-and-Solve, Reflection,
and Research agents together with builders, listeners, approval strategies,
and orchestration support.

## Main packages

- `react`, `plan`, `reflection`, and `research` contain agent strategies.
- `builder` provides fluent agent construction.
- `orchestration` coordinates agent execution.
- `spi` defines listener and approval extension points.
- `annotation` contains annotations used by agent integrations.

Agents use provider, context, tool, and core contracts supplied by sibling
Andalus modules. Select an implementation according to the required reasoning
strategy and inject provider, tools, memory, and listeners through the builder
or the agent API.

## Agent capability discovery

The `capability` package defines framework-level discovery contracts:

- `AgentCapability` is a stable, namespaced capability descriptor.
- `AgentProfile` is an immutable, non-sensitive description of an agent and
  its advertised capabilities.
- `AgentDirectory` defines registration and lookup by agent ID or capability.

Profiles describe advertised functionality only. They do not grant permission
to invoke an agent. Runtime implementations must independently resolve agent
availability and enforce delegation authorization before execution.


## Live runtime registration

The `capability` package also defines `AgentRuntimeRegistrationStore`, a
persistence-neutral contract for live runtime ownership. It separates a
logical agent profile from individual runtime instances and defines atomic
acquire/takeover, lease renewal, readiness publication, and fenced revocation.

A store implementation is responsible for authoritative expiry decisions and
durable monotonic fencing generations. Distributed implementations should use
their shared persistence clock rather than a caller's JVM clock. The framework
contract does not prescribe JDBC, a schema, CDI, or a database vendor.

A registration is an availability signal, not an invocation permission.
Consumers must still apply tenant isolation and authorization before invoking
an advertised agent.
