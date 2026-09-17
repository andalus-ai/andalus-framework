package tech.kayys.wayang.harness.governance.policy;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessSession;
import tech.kayys.wayang.harness.environment.HarnessEnvironment;
import tech.kayys.wayang.harness.workspace.WorkspaceId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record DefaultPolicyContext(
        HarnessIdentity identity,
        HarnessSession session,
        HarnessEnvironment environment,
        Optional<WorkspaceId> workspace,
        Map<String, Object> attributes
) implements PolicyContext {

    public DefaultPolicyContext {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(session, "session");
        Objects.requireNonNull(environment, "environment");
        workspace = workspace == null ? Optional.empty() : workspace;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
