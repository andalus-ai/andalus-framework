# Wayang Project

The `wayang-project` module models projects and sessions used to group agent
work. It provides project and session identities, stores, persistence
strategies, and lifecycle metadata.

Use `ProjectStore` and the session contracts to isolate workspaces, preserve
conversation state, and select the persistence strategy required by a runtime.
