# Wayang Local Protocol

The `wayang-protocol-local` module implements in-process agent communication.
It adapts the communication API to local endpoints without network transport,
making it useful for embedded agents, tests, and same-process orchestration.

Register the local protocol with the communication registry and address agents
through `LocalAgentEndpoint`.
