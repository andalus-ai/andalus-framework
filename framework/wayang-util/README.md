# Wayang Util

The `wayang-util` module is reserved for dependency-light utilities shared by
Wayang framework modules. It is suitable for helpers that do not belong to a
domain-specific API or runtime integration.

Keep utilities stateless and broadly reusable; domain contracts should remain
in their owning module.
