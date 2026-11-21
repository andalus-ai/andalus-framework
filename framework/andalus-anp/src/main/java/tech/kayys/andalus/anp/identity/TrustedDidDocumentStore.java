package tech.kayys.andalus.anp.identity;

import java.util.Optional;

/**
 * Durable boundary for locally trusted DID documents.
 *
 * <p>{@link #register(TrustedDidDocument)} must enforce monotonic revisions
 * atomically across all callers sharing the store. Implementations must accept
 * an identical replay at the current revision, reject different content at the
 * same revision, and reject lower revisions. A successful call returns the
 * canonical stored snapshot.
 *
 * <p>Only explicitly authorized local registration flows may call this API.
 * Remote DID resolution must never write to this store implicitly.
 */
public interface TrustedDidDocumentStore {

    Optional<TrustedDidDocument> find(String did);

    TrustedDidDocument register(TrustedDidDocument candidate);
}
