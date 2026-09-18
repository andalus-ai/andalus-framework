# Wayang Vector

The `wayang-vector` module defines vector-store and embedding-provider
contracts for semantic retrieval. It provides the backend-neutral boundary used
by RAG, memory, and knowledge integrations.

Implement the vector store for the selected database and enforce consistent
embedding dimensions and model versions across writes and queries.
