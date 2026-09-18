# Wayang A2A

The `wayang-a2a` module adapts Wayang agents to Agent-to-Agent (A2A)
communication. It contains agent cards, message and task models, JSON-RPC
transport types, client/server contracts, execution bridges, and durable task
support.

## Main packages

- `adapter` maps between Wayang communication objects and A2A messages.
- `api` exposes A2A client, server, and registry contracts.
- `descriptor` models agent cards, skills, capabilities, and endpoint bindings.
- `execution` connects A2A tasks to the Wayang execution API.
- `durable` persists remote task and wait-state transitions.
- `tool` exposes a remote A2A agent as a Wayang tool.

## Usage

Applications normally depend on `A2AClient`, `A2AServer`, and
`A2AAgentCardService`, then provide a transport and execution bridge. Use the
durable adapter when remote work must survive process restarts.

This module depends on the communication, security, core, agent, extension, and
tool contracts; it does not require a particular HTTP server.
