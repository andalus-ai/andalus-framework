# Wayang Communication Core

The `wayang-communication-core` module provides default communication
implementations over the protocol-neutral API. It includes an agent
communicator, protocol registry, protocol router, selection policy, candidate
model, and task controller.

Register supported protocols in `DefaultProtocolRegistry`, configure selection
through `DefaultProtocolSelectionPolicy`, and use `DefaultAgentCommunicator` as
the application entry point.
