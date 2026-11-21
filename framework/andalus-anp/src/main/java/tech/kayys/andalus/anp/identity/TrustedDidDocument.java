package tech.kayys.andalus.anp.identity;

import java.util.Objects;

/**
 * Immutable locally trusted DID document and its administrator-assigned revision.
 *
 * <p>The revision is trust metadata, not a claim read from the DID document.
 * Durable implementations should persist the document and revision atomically.
 */
public record TrustedDidDocument(DidWbaDocument document, long revision) {
    public TrustedDidDocument {
        Objects.requireNonNull(document, "document");
        if (revision <= 0) {
            throw new IllegalArgumentException("revision must be positive");
        }
    }
}
