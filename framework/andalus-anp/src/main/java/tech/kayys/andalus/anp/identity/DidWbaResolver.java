package tech.kayys.andalus.anp.identity;

import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/** Resolves DID:WBA identities through a validated local registry and optional remote fetcher. */
public final class DidWbaResolver {
    public static final Duration DEFAULT_FETCH_TIMEOUT = Duration.ofSeconds(3);
    public static final int DEFAULT_MAX_RESPONSE_BYTES = 1_048_576;

    private final Map<String, TrustedDidDocument> registry = new ConcurrentHashMap<>();
    private final DidWbaDocumentFetcher fetcher;
    private final Duration fetchTimeout;
    private final int maxResponseBytes;

    /** Creates a local-only resolver. */
    public DidWbaResolver() {
        this(null, DEFAULT_FETCH_TIMEOUT, DEFAULT_MAX_RESPONSE_BYTES);
    }

    /**
     * Creates a resolver with an optional remote fetcher. Fetcher implementations
     * must enforce timeout, response-size, HTTP status, and deployment egress controls.
     */
    public DidWbaResolver(DidWbaDocumentFetcher fetcher, Duration fetchTimeout, int maxResponseBytes) {
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

    /**
     * Registers an initial locally trusted document at revision 1.
     * Subsequent changes must use an explicit, strictly increasing revision.
     */
    public void register(DidWbaDocument document) {
        register(document, 1);
    }

    /**
     * Registers a document through the explicit local trust boundary.
     *
     * <p>Revisions are monotonic per DID. Replaying the same document at the same
     * revision is idempotent; changing content without increasing the revision,
     * or attempting to move backwards, is rejected. The compute operation makes
     * the comparison and replacement atomic within this resolver instance.
     *
     * <p>This method does not establish provenance by itself: callers must only
     * invoke it after authenticating the administrator, trust anchor, or
     * synchronization channel authorized to approve this DID update.
     */
    public void register(DidWbaDocument document, long revision) {
        Objects.requireNonNull(document, "document");
        if (revision <= 0) {
            throw new IllegalArgumentException("DID document revision must be positive");
        }
        validate(document);
        registry.compute(document.id(), (did, current) -> {
            if (current == null) {
                return new TrustedDidDocument(document, revision);
            }
            if (revision < current.revision()) {
                throw new SecurityException("Stale DID document revision rejected for " + did);
            }
            if (revision == current.revision()) {
                if (current.document().equals(document)) {
                    return current;
                }
                throw new SecurityException("DID document content changed without a revision increase for " + did);
            }
            return new TrustedDidDocument(document, revision);
        });
    }

    /** Looks up a document registered through the trusted local registration boundary. */
    public Optional<DidWbaDocument> findRegistered(DidWbaIdentity identity) {
        return findRegisteredVersioned(identity).map(TrustedDidDocument::document);
    }

    /** Returns the locally trusted document together with its monotonic revision. */
    public Optional<TrustedDidDocument> findRegisteredVersioned(DidWbaIdentity identity) {
        Objects.requireNonNull(identity, "identity");
        return Optional.ofNullable(registry.get(identity.raw()));
    }

    /**
     * Resolves locally first, then optionally fetches from the DID-derived HTTPS URL.
     * Remote documents are validated but not automatically cached or trusted for enrollment.
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
