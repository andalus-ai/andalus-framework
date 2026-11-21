package tech.kayys.andalus.anp.identity;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Thread-safe in-memory implementation for tests and single-process deployments. */
public final class InMemoryTrustedDidDocumentStore implements TrustedDidDocumentStore {
    private final ConcurrentMap<String, TrustedDidDocument> documents = new ConcurrentHashMap<>();

    @Override
    public Optional<TrustedDidDocument> find(String did) {
        return Optional.ofNullable(documents.get(Objects.requireNonNull(did, "did")));
    }

    @Override
    public TrustedDidDocument register(TrustedDidDocument candidate) {
        Objects.requireNonNull(candidate, "candidate");
        String did = candidate.document().id();
        return documents.compute(did, (key, current) -> {
            if (current == null) {
                return candidate;
            }
            if (candidate.revision() < current.revision()) {
                throw new SecurityException("Stale DID document revision rejected for " + key);
            }
            if (candidate.revision() == current.revision()) {
                if (current.document().equals(candidate.document())) {
                    return current;
                }
                throw new SecurityException("DID document content changed without a revision increase for " + key);
            }
            return candidate;
        });
    }
}
