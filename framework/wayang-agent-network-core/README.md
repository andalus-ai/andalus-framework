# Wayang Agent Network Core

The `wayang-agent-network-core` module supplies default implementations for the
agent-network contracts. It provides protocol registration, candidate ranking,
network coordination, and an in-process local network adapter.

## Main packages

- `core` contains `DefaultAgentNetwork` and protocol registries.
- `core.router` ranks eligible network candidates.
- `core.local` implements local client and protocol behavior.

Use this module when an application needs a working network coordinator without
implementing the protocol-neutral APIs itself. Register additional protocols
through `AgentNetworkProtocolRegistry`.
