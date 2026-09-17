package tech.kayys.wayang.harness.environment;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.context.HarnessSession;

import java.util.Objects;

/**
 * Default implementation of {@link HarnessEnvironment}.
 */
public record DefaultHarnessEnvironment(
        HarnessIdentity identity,
        HarnessSession session,
        HarnessCapabilities capabilities,
        HarnessResources resources
) implements HarnessEnvironment {

    public DefaultHarnessEnvironment {
        Objects.requireNonNull(identity, "identity");
        Objects.requireNonNull(session, "session");
        Objects.requireNonNull(capabilities, "capabilities");
        Objects.requireNonNull(resources, "resources");
    }
}
