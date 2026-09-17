package tech.kayys.wayang.harness.governance.policy;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessSession;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.util.Map;
import java.util.Optional;

/**
 * Contextual state provided to policies during evaluation.
 */
public interface PolicyContext {

    HarnessIdentity identity();

    HarnessSession session();

    HarnessEnvironment environment();

    Optional<WorkspaceId> workspace();

    Map<String, Object> attributes();
}
