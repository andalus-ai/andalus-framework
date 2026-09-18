# Wayang Messaging

The `wayang-messaging` module defines the message-queue service SPI used to
publish and consume asynchronous agent events and work items.

Implement the messaging service for the broker used by the runtime and keep
application code dependent on the module's message and subscription contracts.
