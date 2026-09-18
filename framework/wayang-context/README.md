# Wayang Context

The `wayang-context` module compiles source context for agent requests. Its
three-tier model can move from full source to structural skeletons and compact
digests while estimating tokens, scoring relevance, resolving symbols, and
planning chunks.

## Main packages

- `api` defines compiler, planner, scorer, skeletonizer, resolver, and estimator
  contracts.
- `api.model` contains plans, tiers, chunks, indexes, and compiled context.
- `impl` provides JavaParser-backed and heuristic implementations.

Use the API contracts in application code and select the default implementations
for local source repositories. Configure budgets and task intent through the
model types rather than coupling callers to parser internals.
