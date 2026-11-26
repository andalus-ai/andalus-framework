package tech.kayys.andalus.anp.identity;

import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/** Resolves DID:WBA identities through a trusted store and optional remote fetcher. */
public final class DidWbaResolver {
    public static final Duration DEFAULT_FETCH_TIMEOUT = Duration.ofSeconds(3);
    public static final int DEFAULT_MAX_RESPONSE_BYTES = 1_048_576;

    private final TrustedDidDocumentStore store;
    private final DidWbaDocumentFetcher fetcher;
    private final Duration fetchTimeout;
    private final int maxResponseBytes;

    /** Creates a local-only resolver backed by an in-memory store. */
    public DidWbaResolver() {
        this(new InMemoryTrustedDidDocumentStore(), null, DEFAULT_FETCH_TIMEOUT, DEFAULT_MAX_RESPONSE_BYTES);
    }

    /** Creates a resolver backed by the supplied trusted store. */
    public DidWbaResolver(TrustedDidDocumentStore store) {
        this(store, null, DEFAULT_FETCH_TIMEOUT, DEFAULT_MAX_RESPONSE_BYTES);
    }

    /** Creates a resolver with an optional remote fetcher and in-memory trusted store. */
    public DidWbaResolver(DidWbaDocumentFetcher fetcher, Duration fetchTimeout, int maxResponseBytes) {
        this(new InMemoryTrustedDidDocumentStore(), fetcher, fetchTimeout, maxResponseBytes);
    }

    /**
     * Creates a resolver with an explicit trusted store and optional remote fetcher.
     * Fetcher implementations must enforce timeout, response-size, HTTP status,
     * and deployment egress controls.
     */
    public DidWbaResolver(TrustedDidDocumentStore store, DidWbaDocumentFetcher fetcher,
            Duration fetchTimeout, int maxResponseBytes) {
        this.store = Objects.requireNonNull(store, "store");
        this.fetcher = fetcher;
        this.fetchTimeout = Objects.requireNonNull(fetchTimeout, "fetchTimeout");
        if (fetchTimeout.isZero() || fetchTimeout.isNegative()) {
            throw new IllegalArgumentException("fetchTimeout must be positive");
        }
        if (maxResponseBytes <= 0) {
            throw new IllegalArgumentException("maxResponseBytes must be positive");
        }
        this.maxResponseBytes = maxResponseBytes;
    }

    /** Registers an initial locally trusted document at revision 1. */
    public void register(DidWbaDocument document) {
        register(document, 1);
    }

    /**
     * Registers a document through the explicit local trust boundary.
     * The store must enforce revision checks atomically across runtime instances.
     * Callers must authenticate the administrator, trust anchor, or synchronization
     * channel authorized to approve this update before invoking this method.
     */
    public void register(DidWbaDocument document, long revision) {
        Objects.requireNonNull(document, "document");
        if (revision <= 0) {
            throw new IllegalArgumentException("DID document revision must be positive");
        }
        validate(document);
        store.register(new TrustedDidDocument(document, revision));
    }

    public Optional<DidWbaDocument> findRegistered(DidWbaIdentity identity) {
        return findRegisteredVersioned(identity).map(TrustedDidDocument::document);
    }

    public Optional<TrustedDidDocument> findRegisteredVersioned(DidWbaIdentity identity) {
        Objects.requireNonNull(identity, "identity");
        return store.find(identity.raw());
    }

    /**
     * Resolves locally first, then optionally fetches from the DID-derived HTTPS URL.
     * Remote documents are validated but never automatically written to the trusted store.
     */
    public CompletionStage<Optional<DidWbaDocument>> resolve(DidWbaIdentity identity) {
        Objects.requireNonNull(identity, "identity");
        Optional<TrustedDidDocument> local = findRegisteredVersioned(identity);
        if (local.isPresent()) {
            return CompletableFuture.completedFuture(local.map(TrustedDidDocument::document));
        }
        if (fetcher == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        URI url = identity.toDidDocumentUrl();
        validateResolutionUrl(identity, url);
        final CompletionStage<Optional<DidWbaDocument>> fetched;
        try {
            fetched = Objects.requireNonNull(fetcher.fetch(url, fetchTimeout, maxResponseBytes),
                    "fetcher returned null CompletionStage");
        } catch (RuntimeException exception) {
            return CompletableFuture.failedFuture(exception);
        }
        return fetched.thenApply(result -> {
            if (result == null || result.isEmpty()) {
                return Optional.empty();
            }
            DidWbaDocument document = result.get();
            validate(document);
            if (!identity.raw().equals(document.id())) {
                throw new SecurityException("Resolved DID document subject does not match requested DID");
            }
            return Optional.of(document);
        });
    }

    private static void validateResolutionUrl(DidWbaIdentity identity, URI uri) {
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null
                || !uri.getHost().equalsIgnoreCase(identity.host())
                || uri.getUserInfo() != null || uri.getFragment() != null) {
            throw new SecurityException("DID resolution URL must be HTTPS for the DID authority");
        }
    }

    private static void validate(DidWbaDocument document) {
        String did = document.id();
        if (did.isBlank() || !did.startsWith(DidWbaIdentity.PREFIX)) {
            throw new IllegalArgumentException("DID document id must be a non-blank DID:WBA identifier");
        }
        DidWbaIdentity.parse(did);
        Set<String> methodIds = new HashSet<>();
        for (DidWbaVerificationMethod method : document.verificationMethods()) {
            if (method == null || method.id().isBlank() || method.controller().isBlank()) {
                throw new IllegalArgumentException("Verification methods require non-blank id and controller");
            }
            String prefix = did + "#";
            if (!method.id().startsWith(prefix) || method.id().length() == prefix.length()) {
                throw new IllegalArgumentException("Verification method id must be a fragment of the document DID: " + method.id());
            }
            if (!did.equals(method.controller())) {
                throw new IllegalArgumentException("Verification method controller must match the document DID: " + method.id());
            }
            if (!methodIds.add(method.id())) {
                throw new IllegalArgumentException("Duplicate verification method id: " + method.id());
            }
        }
        for (String reference : document.authentication()) {
            if (reference == null || reference.isBlank()) {
                throw new IllegalArgumentException("Authentication references must be non-blank");
            }
            String absoluteId = reference.startsWith("#") ? did + reference : reference;
            if (!methodIds.contains(absoluteId)) {
                throw new IllegalArgumentException("Authentication must reference a declared verification method: " + reference);
            }
        }
    }
}
