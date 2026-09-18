# Wayang Provider

The `wayang-provider` module defines model-provider and inference contracts. It
covers providers, model catalogs, direct and adaptive routing, streaming
events, and provider responses.

Register providers in the model catalog and select them through `ModelRouter`.
Keep provider credentials and transport configuration in the runtime rather
than in agent definitions.
