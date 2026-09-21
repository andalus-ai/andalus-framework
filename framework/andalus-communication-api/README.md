# Andalus Communication API

The `andalus-communication-api` module defines protocol-neutral agent
communication contracts. It models agents, endpoints, capabilities, messages,
requests and responses, tasks, protocol clients and servers, and communication
failures.

Use this module at integration boundaries. Concrete routing and selection are
provided by `andalus-communication-core`; protocol adapters implement
`AgentProtocol`, `ProtocolClient`, and `ProtocolServer`.
