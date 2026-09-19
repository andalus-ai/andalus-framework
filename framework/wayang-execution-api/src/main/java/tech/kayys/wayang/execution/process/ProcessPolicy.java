package tech.kayys.wayang.execution.process;

import java.util.Set;

/**
 * Governs executable launches within a sandbox.
 */
public interface ProcessPolicy {

    ProcessDecision evaluate(ProcessRequest request);

    static ProcessPolicy allowAll() {
        return request -> ProcessDecision.allow("Process execution unrestricted");
    }

    static ProcessPolicy allowList(Set<String> allowedBinaries) {
        Set<String> set = Set.copyOf(allowedBinaries);
        return request -> {
            if (set.contains(request.executable().name())) {
                return ProcessDecision.allow("Binary explicitly allowed: " + request.executable().name());
            }
            return ProcessDecision.deny("Binary not in allowlist: " + request.executable().name());
        };
    }
}
