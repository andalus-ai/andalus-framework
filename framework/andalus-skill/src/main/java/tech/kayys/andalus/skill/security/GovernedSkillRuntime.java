package tech.kayys.andalus.skill.security;

import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

/**
 * Decorating {@link SkillRuntime} enforcing policy and security checks before
 * delegating to an underlying runtime.
 */
public final class GovernedSkillRuntime implements SkillRuntime {

    private final SkillRuntime delegate;
    private final SkillSecurityPolicy policy;
    private final Supplier<SkillSecurityContext> contextSupplier;

    public GovernedSkillRuntime(SkillRuntime delegate, SkillSecurityPolicy policy) {
        this(delegate, policy, SkillSecurityContext::unauthenticated);
    }

    public GovernedSkillRuntime(
            SkillRuntime delegate,
            SkillSecurityPolicy policy,
            Supplier<SkillSecurityContext> contextSupplier) {

        this.delegate        = Objects.requireNonNull(delegate, "delegate must not be null");
        this.policy          = Objects.requireNonNull(policy, "policy must not be null");
        this.contextSupplier = Objects.requireNonNull(contextSupplier, "contextSupplier must not be null");
    }

    @Override
    public CompletionStage<SkillResult> execute(SkillExecutionRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        SkillSecurityContext securityContext = contextSupplier.get();
        SkillSecurityDecision decision = policy.evaluate(request, securityContext);

        if (!decision.allowed()) {
            return CompletableFuture.completedFuture(
                    SkillResult.failure(
                            request.skill().id(),
                            "Security policy denied invocation: " + decision.reason()
                    )
            );
        }

        return delegate.execute(request);
    }

    public SkillRuntime delegate() {
        return delegate;
    }

    public SkillSecurityPolicy policy() {
        return policy;
    }
}
