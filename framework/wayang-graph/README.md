# Wayang Graph

The `wayang-graph` module defines the graph-store SPI for knowledge graphs. It
models nodes, relationships, queries, results, statistics, and the
`GraphStore` abstraction.

Implement `GraphStore` for the graph database or index used by the host
application. Keep graph queries and result mapping behind this SPI so knowledge
and retrieval modules remain backend independent.
