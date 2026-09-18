# Wayang Command

The `wayang-command` module defines the command-discovery contract used by
Wayang workbenches and command palettes. It provides the command API, discovery
service, and JSON schema models for describing commands to clients.

Implement `WayangCommandDiscoveryService` in the host application and expose
the resulting contract or schema to the workbench integration.
