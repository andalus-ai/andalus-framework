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
