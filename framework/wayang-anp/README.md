# Wayang ANP

The `wayang-anp` module is an optional adapter for Agent Network Protocol
(ANP) 1.1. It implements DID:WBA identity, agent descriptions, discovery,
capability advertisement and selection, meta-protocol negotiation, and HTTP
signature authentication.

## Main packages

- `identity` models DID:WBA documents, keys, and resolution.
- `description` publishes agent and capability descriptions.
- `discovery` performs ANP discovery.
- `meta` negotiates advertised capabilities.
- `auth` and `security` verify protocol and principal information.
- `client` integrates ANP with the agent-network API.

ANP is intentionally optional. Depend on this module only when an application
needs ANP interoperability; keep application services on the
`wayang-agent-network-api` contracts.
