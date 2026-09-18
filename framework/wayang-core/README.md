# Wayang Core

The `wayang-core` module contains the foundational contracts shared by Wayang
agents and runtimes. It models agent definitions and descriptors, agent
contexts and requests, prompts, input and output providers, execution status,
and runtime boundaries.

This is the lowest-level agent contract module. Depend on it when implementing
an agent, provider, runtime, or integration that should remain independent of
the higher-level execution and orchestration modules.
