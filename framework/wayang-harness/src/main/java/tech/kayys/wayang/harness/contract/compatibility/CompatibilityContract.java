package tech.kayys.wayang.harness.contract.compatibility;

import java.util.List;
import tech.kayys.wayang.harness.contract.version.ContractVersion;

public interface CompatibilityContract {

    boolean supports(ContractVersion version);

    CompatibilityResult evaluate(tech.kayys.wayang.harness.contract.AgentContract remote);

    static CompatibilityContract semanticMajorMatch(ContractVersion current) {
        return new CompatibilityContract() {
            @Override
            public boolean supports(ContractVersion version) {
                return current.isCompatibleWith(version);
            }

            @Override
            public CompatibilityResult evaluate(tech.kayys.wayang.harness.contract.AgentContract remote) {
                if (remote == null) {
                    return CompatibilityResult.failure(List.of("Remote contract is null"));
                }
                if (!supports(remote.version())) {
                    return CompatibilityResult.failure(List.of("Incompatible contract version: " + remote.version() + ", requires: " + current));
                }
                return CompatibilityResult.success();
            }
        };
    }
}
