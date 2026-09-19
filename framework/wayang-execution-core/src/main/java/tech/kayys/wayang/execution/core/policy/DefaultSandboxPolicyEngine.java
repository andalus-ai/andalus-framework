package tech.kayys.wayang.execution.core.policy;

import tech.kayys.wayang.execution.filesystem.AccessDecision;
import tech.kayys.wayang.execution.filesystem.FilesystemOperation;
import tech.kayys.wayang.execution.filesystem.FilesystemPolicy;
import tech.kayys.wayang.execution.filesystem.PathReference;
import tech.kayys.wayang.execution.network.NetworkMode;
import tech.kayys.wayang.execution.network.NetworkPolicy;
import tech.kayys.wayang.execution.network.NetworkRule;
import tech.kayys.wayang.execution.policy.PolicyDecision;
import tech.kayys.wayang.execution.policy.SandboxOperation;
import tech.kayys.wayang.execution.policy.SandboxPolicyEngine;
import tech.kayys.wayang.execution.process.ExecutableReference;
import tech.kayys.wayang.execution.process.ProcessDecision;
import tech.kayys.wayang.execution.process.ProcessPolicy;
import tech.kayys.wayang.execution.process.ProcessRequest;
import tech.kayys.wayang.execution.sandbox.SandboxContext;
import tech.kayys.wayang.execution.sandbox.SandboxSpec;
import tech.kayys.wayang.execution.secrets.SecretId;
import tech.kayys.wayang.execution.secrets.SecretPolicy;
import tech.kayys.wayang.execution.secrets.SecretReference;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Composite Policy Decision Point (PDP) that enforces sandbox policy rules.
 */
public class DefaultSandboxPolicyEngine implements SandboxPolicyEngine {

    private final SandboxSpec specification;

    public DefaultSandboxPolicyEngine(SandboxSpec specification) {
        this.specification = Objects.requireNonNull(specification, "SandboxSpec cannot be null");
    }

    @Override
    public PolicyDecision evaluate(SandboxOperation operation, SandboxContext context) {
        Objects.requireNonNull(operation, "SandboxOperation cannot be null");
        String type = operation.operationType().toUpperCase();

        return switch (type) {
            case "FILESYSTEM", "FS" -> evaluateFilesystem(operation);
            case "PROCESS", "EXEC" -> evaluateProcess(operation);
            case "NETWORK", "NET" -> evaluateNetwork(operation);
            case "SECRET" -> evaluateSecret(operation, context);
            default -> PolicyDecision.deny("Unknown operation type: " + type);
        };
    }

    private PolicyDecision evaluateFilesystem(SandboxOperation operation) {
        FilesystemPolicy policy = specification.filesystem();
        String opName = (String) operation.parameters().getOrDefault("op", "READ");
        FilesystemOperation fsOp;
        try {
            fsOp = FilesystemOperation.valueOf(opName.toUpperCase());
        } catch (IllegalArgumentException e) {
            fsOp = FilesystemOperation.READ;
        }

        PathReference path = PathReference.workspace(operation.target());
        AccessDecision decision = policy.evaluate(fsOp, path);
        if (decision.isAllowed()) {
            return PolicyDecision.allow(decision.reason());
        }
        return PolicyDecision.deny(decision.reason());
    }

    private PolicyDecision evaluateProcess(SandboxOperation operation) {
        ProcessPolicy policy = specification.process();
        ExecutableReference execRef = ExecutableReference.of(operation.target());
        ProcessRequest request = new ProcessRequest(execRef, List.of(), null, null);
        ProcessDecision decision = policy.evaluate(request);
        if (decision.isAllowed()) {
            return PolicyDecision.allow(decision.reason());
        }
        return PolicyDecision.deny(decision.reason());
    }

    private PolicyDecision evaluateNetwork(SandboxOperation operation) {
        NetworkPolicy policy = specification.network();
        if (policy.mode() == NetworkMode.NONE) {
            return PolicyDecision.deny("Network access completely disabled in sandbox");
        }
        if (policy.mode() == NetworkMode.FULL) {
            return PolicyDecision.allow("Full network access enabled");
        }
        if (policy.mode() == NetworkMode.LOOPBACK_ONLY) {
            String host = operation.target();
            if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)) {
                return PolicyDecision.allow("Loopback traffic allowed");
            }
            return PolicyDecision.deny("Egress blocked: only loopback allowed");
        }
        if (policy.mode() == NetworkMode.ALLOWLIST) {
            String target = operation.target();
            for (NetworkRule rule : policy.rules()) {
                if (rule.hostOrCidr().equalsIgnoreCase(target) || "*".equals(rule.hostOrCidr())) {
                    if (rule.allow()) {
                        return PolicyDecision.allow("Matched allowlist rule for host: " + target);
                    }
                }
            }
            return PolicyDecision.deny("Host not in network allowlist: " + target);
        }
        return PolicyDecision.deny("Network policy denied: " + policy.mode());
    }

    private PolicyDecision evaluateSecret(SandboxOperation operation, SandboxContext context) {
        SecretPolicy policy = specification.secrets();
        SecretReference ref = SecretReference.of(SecretId.of(operation.target()), null);
        boolean permitted = policy.isPermitted(ref, context);
        if (permitted) {
            return PolicyDecision.allow("Secret access permitted");
        }
        return PolicyDecision.deny("Secret access denied: " + operation.target());
    }
}
