package tech.kayys.wayang.execution.secrets;

import tech.kayys.wayang.execution.sandbox.SandboxContext;

import java.util.Set;

/**
 * Policy governing whether a secret reference can be resolved or injected.
 */
public interface SecretPolicy {

    boolean isPermitted(SecretReference reference, SandboxContext context);

    static SecretPolicy denyAll() {
        return (ref, ctx) -> false;
    }

    static SecretPolicy allowReferences(Set<SecretId> permittedSecrets) {
        Set<SecretId> set = Set.copyOf(permittedSecrets);
        return (ref, ctx) -> set.contains(ref.id());
    }
}
