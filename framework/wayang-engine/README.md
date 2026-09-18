# Wayang Engine

The `wayang-engine` module provides the execution graph and scheduling
foundation for agentic workflows. It includes execution nodes and edges,
conditions, variables, retries, snapshots, checkpoints, event buses,
planners, reasoners, triggers, and service registries.

Build an `ExecutionGraph` from typed nodes and conditions, then provide an
`ExecutionEngine` and scheduler. Use snapshots and checkpoint stores when
execution must be resumed or inspected.
