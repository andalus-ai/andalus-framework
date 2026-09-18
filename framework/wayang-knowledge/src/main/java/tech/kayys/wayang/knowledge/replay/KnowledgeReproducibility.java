package tech.kayys.wayang.knowledge.replay;

/**
 * Represents a knowledge reproducibility.
 *
 * <p>Its components capture `evidence reproducible`, `governance reproducible`, `policy reproducible`, `rule reproducible`, `decision reproducible`, and other values.</p>
 *
 * @param evidenceReproducible the evidence reproducible
 * @param governanceReproducible the governance reproducible
 * @param policyReproducible the policy reproducible
 * @param ruleReproducible the rule reproducible
 * @param decisionReproducible the decision reproducible
 * @param modelOutputReproducible the model output reproducible
 * @param explanation the explanation
 */


public record KnowledgeReproducibility(
        boolean evidenceReproducible,
        boolean governanceReproducible,
        boolean policyReproducible,
        boolean ruleReproducible,
        boolean decisionReproducible,
        boolean modelOutputReproducible,
        String explanation
) {

    public boolean fullyReproducible() {
        return evidenceReproducible
                && governanceReproducible
                && policyReproducible
                && ruleReproducible
                && decisionReproducible;
    }
}
