package tech.kayys.andalus.harness.protocol;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Default implementation of {@link DecisionEngine} that coordinates strategy invocation,
 * validates candidate decisions, and converts errors into controlled decision outcomes.
 */
public final class DefaultDecisionEngine implements DecisionEngine {

    private final DecisionStrategy strategy;
    private final DecisionValidator validator;
    private final Optional<DecisionStrategy> fallbackStrategy;

    public DefaultDecisionEngine(DecisionStrategy strategy) {
        this(strategy, new DefaultDecisionValidator(), null);
    }

    public DefaultDecisionEngine(DecisionStrategy strategy, DecisionValidator validator) {
        this(strategy, validator, null);
    }

    public DefaultDecisionEngine(
            DecisionStrategy strategy,
            DecisionValidator validator,
            DecisionStrategy fallbackStrategy
    ) {
        this.strategy = Objects.requireNonNull(strategy, "strategy cannot be null");
        this.validator = Objects.requireNonNull(validator, "validator cannot be null");
        this.fallbackStrategy = Optional.ofNullable(fallbackStrategy);
    }

    @Override
    public CompletionStage<AgentDecision> decide(DecisionContext context) {
        Objects.requireNonNull(context, "DecisionContext cannot be null");

        return invokeStrategy(strategy, context)
                .handle((decision, error) -> {
                    if (error != null) {
                        if (fallbackStrategy.isPresent()) {
                            return invokeStrategy(fallbackStrategy.get(), context)
                                    .handle((fbDecision, fbError) -> {
                                        if (fbError != null) {
                                            return AgentDecision.fail("Decision strategy and fallback failed: "
                                                    + fbError.getClass().getSimpleName() + ": " + fbError.getMessage());
                                        }
                                        return validateDecision(fbDecision, context);
                                    });
                        }
                        return CompletableFuture.completedFuture(
                                AgentDecision.fail("Decision strategy failed: "
                                        + error.getClass().getSimpleName() + ": " + error.getMessage())
                        );
                    }
                    return CompletableFuture.completedFuture(validateDecision(decision, context));
                })
                .thenCompose(stage -> stage);
    }

    private CompletionStage<AgentDecision> invokeStrategy(DecisionStrategy strat, DecisionContext context) {
        try {
            CompletionStage<AgentDecision> stage = strat.decide(context);
            if (stage == null) {
                return CompletableFuture.completedFuture(
                        AgentDecision.fail("Decision strategy returned null CompletionStage")
                );
            }
            return stage;
        } catch (RuntimeException ex) {
            return CompletableFuture.completedFuture(
                    AgentDecision.fail("Decision strategy invocation threw exception: "
                            + ex.getClass().getSimpleName() + ": " + ex.getMessage())
            );
        }
    }

    private AgentDecision validateDecision(AgentDecision decision, DecisionContext context) {
        if (decision == null) {
            return AgentDecision.fail("Decision strategy returned null decision");
        }

        DecisionValidationResult result = validator.validate(decision, context);
        if (!result.valid()) {
            return AgentDecision.fail("Invalid decision: " + result.reason());
        }

        return decision;
    }
}
