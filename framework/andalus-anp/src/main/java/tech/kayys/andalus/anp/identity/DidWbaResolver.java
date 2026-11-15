package tech.kayys.andalus.anp.identity;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves a DID:WBA identity into a {@link DidWbaDocument}.
 *
 * <p>Supports locally registered, structurally validated documents. Remote
 * resolution is deliberately not implied by this registry implementation.
 */
public final class DidWbaResolver {

    private final Map<String, DidWbaDocument> registry = new ConcurrentHashMap<>();

    /**
     * Registers a locally trusted DID document after checking its subject,
     * verification-method controllers, identifiers, and authentication links.
     *
     * <p>This is structural validation only. The caller remains responsible
     * for obtaining the document through an authenticated DID resolution and
     * deciding whether its publication/key enrollment is trusted.
     *
     * @throws IllegalArgumentException if the document is inconsistent
     */
    public void register(DidWbaDocument document) {
        Objects.requireNonNull(document, "document");
        validate(document);
        registry.put(document.id(), document);
    }

    /** Resolves a DID document. Checks local registry first. */
    public CompletionStage<Optional<DidWbaDocument>> resolve(DidWbaIdentity identity) {
        Objects.requireNonNull(identity, "identity");
        DidWbaDocument doc = registry.get(identity.raw());
        if (doc != null) {
            return CompletableFuture.completedFuture(Optional.of(doc));
        }
        // Network resolution requires an explicitly configured trusted transport.
        return CompletableFuture.completedFuture(Optional.empty());
    }

    private static void validate(DidWbaDocument document) {
        String did = document.id();
        if (did.isBlank() || !did.startsWith(DidWbaIdentity.PREFIX)) {
            throw new IllegalArgumentException("DID document id must be a non-blank DID:WBA identifier");
        }
        // Parse as well as compare so malformed DID strings cannot enter the registry.
        DidWbaIdentity.parse(did);

        Set<String> methodIds = new HashSet<>();
        for (DidWbaVerificationMethod method : document.verificationMethods()) {
            if (method == null || method.id().isBlank() || method.controller().isBlank()) {
                throw new IllegalArgumentException("Verification methods require non-blank id and controller");
            }
            String expectedPrefix = did + "#";
            if (!method.id().startsWith(expectedPrefix)
                    || method.id().length() == expectedPrefix.length()) {
                throw new IllegalArgumentException("Verification method id must be a fragment of the document DID: " + method.id());
            }
            if (!did.equals(method.controller())) {
                throw new IllegalArgumentException("Verification method controller must match the document DID: " + method.id());
            }
            if (!methodIds.add(method.id())) {
                throw new IllegalArgumentException("Duplicate verification method id: " + method.id());
            }
        }

        for (String authenticationMethod : document.authentication()) {
            if (authenticationMethod == null || authenticationMethod.isBlank()) {
                throw new IllegalArgumentException("Authentication references must be non-blank");
            }
            String absoluteId = authenticationMethod.startsWith("#")
                    ? did + authenticationMethod
                    : authenticationMethod;
            if (!methodIds.contains(absoluteId)) {
                throw new IllegalArgumentException("Authentication must reference a verification method in this DID document: "
                        + authenticationMethod);
            }
        }
    }
}
