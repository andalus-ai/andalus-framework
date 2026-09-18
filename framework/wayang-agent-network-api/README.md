# Wayang Agent Network API

The `wayang-agent-network-api` module defines protocol-neutral contracts for
distributed agent networks. It covers endpoint resolution, agent discovery,
trust evaluation, capability negotiation, and network task lifecycle.

## Main packages

- `protocol` defines network client, server, and protocol contracts.
- `discovery` finds agents from a query.
- `endpoint` resolves a logical agent to an endpoint.
- `capability` negotiates requirements against advertised capabilities.
- `trust` evaluates whether a remote agent may be used.
- `exception` provides typed network failures.

Implementations belong in adapter or core modules such as `wayang-anp` and
`wayang-agent-network-core`; applications should depend on these contracts when
they need to remain protocol independent.
