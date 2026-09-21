package tech.kayys.andalus.knowledge.governance;

import tech.kayys.andalus.knowledge.KnowledgeContext;
import tech.kayys.andalus.knowledge.KnowledgeItem;

/**
 * Evaluates whether knowledge items are permitted to be retrieved or stored under the current governance scope.
 */
public interface KnowledgeGovernanceResolver {

    boolean isPermitted(KnowledgeItem item, KnowledgeContext context);
}
