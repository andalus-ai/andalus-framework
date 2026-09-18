# Wayang Security API

The `wayang-security-api` module defines authentication and authorization
contracts for Wayang. It models security contexts, delegation, obligations,
policy enforcement points, principals, and security decisions.

Depend on this module at security boundaries and provide the policy and
identity implementation through `wayang-security-core` or an application
integration.
