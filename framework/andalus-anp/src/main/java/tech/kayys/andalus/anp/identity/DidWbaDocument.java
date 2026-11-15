package tech.kayys.andalus.anp.identity;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * W3C-compliant DID Document representation for a DID:WBA identity.
 */
public record DidWbaDocument(
        String id,
        List<DidWbaVerificationMethod> verificationMethods,
        List<String> authentication,
        Map<String, String> services
) {
    public DidWbaDocument {
        Objects.requireNonNull(id, "id");
        verificationMethods = verificationMethods == null ? List.of() : List.copyOf(verificationMethods);
        authentication = authentication == null ? List.of() : List.copyOf(authentication);
        services = services == null ? Map.of() : Map.copyOf(services);
    }

    /**
     * Finds a verification method by its exact DID URL or exact fragment.
     * Suffix matching is intentionally avoided to prevent ambiguous key selection.
     */
    public Optional<DidWbaVerificationMethod> findVerificationMethod(String methodId) {
        if (methodId == null || methodId.isBlank()) {
            return Optional.empty();
        }
        String absoluteId = methodId.startsWith("#") ? id + methodId : methodId;
        return verificationMethods.stream()
                .filter(method -> method.id().equals(absoluteId))
                .findFirst();
    }

    /** Returns whether the method is explicitly authorized for authentication. */
    public boolean isAuthenticationMethod(String methodId) {
        Optional<DidWbaVerificationMethod> method = findVerificationMethod(methodId);
        if (method.isEmpty()) {
            return false;
        }
        String absoluteId = method.get().id();
        return authentication.stream().anyMatch(reference ->
                reference.equals(absoluteId)
                        || reference.equals(absoluteId.substring(id.length())));
    }
}
