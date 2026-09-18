# Wayang Configuration

The `wayang-configuration` module models configuration as a reloadable,
typed Wayang resource. It provides builders, registries, change events,
listeners, watchers, and sources for files, environment variables, and system
properties.

Compose sources into a `Configuration`, register it with
`ConfigurationRegistry`, and attach listeners when consumers need to react to
changes. Use the supplied source implementations for common deployment
environments or implement `ConfigurationSource` for a custom backend.
