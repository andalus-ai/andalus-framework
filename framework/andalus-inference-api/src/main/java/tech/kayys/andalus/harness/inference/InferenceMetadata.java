package tech.kayys.andalus.harness.inference;

/**
 * Standard metadata keys shared by inference runtimes and provider adapters.
 *
 * <p>Keys in this class are framework-level contracts. Product-specific
 * orchestration metadata should remain in the request payload and must not
 * be added here.</p>
 */
public final class InferenceMetadata {

    /** Provider-reported reasoning-token count; never infer this from output tokens. */
    public static final String REASONING_TOKENS = "andalus.usage.reasoningTokens";

    /** Optional reasoning-token budget requested by the calling application. */
    public static final String REASONING_TOKEN_BUDGET = "andalus.reasoningTokenBudget";

    private InferenceMetadata() {
    }
}
