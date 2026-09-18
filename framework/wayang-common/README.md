# Wayang Common

The `wayang-common` module contains shared framework primitives that are used
across Wayang modules: error codes and responses, validation contracts and
results, migration registration and version management, and cache support
types.

These types are deliberately small and dependency-light. Use them to keep
cross-module behavior consistent instead of creating local equivalents.
