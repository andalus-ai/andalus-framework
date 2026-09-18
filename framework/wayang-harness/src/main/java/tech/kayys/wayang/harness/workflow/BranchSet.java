package tech.kayys.wayang.harness.workflow;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Maps evaluation outcomes to target branch NodeIds.
 */
public record BranchSet(
        Map<String, NodeId> branches,
        Optional<NodeId> defaultBranch
) {

    public BranchSet {
        branches = branches != null ? Map.copyOf(branches) : Map.of();
        defaultBranch = defaultBranch != null ? defaultBranch : Optional.empty();
    }

    public static BranchSet of(Map<String, NodeId> branches, NodeId defaultBranch) {
        return new BranchSet(branches, Optional.ofNullable(defaultBranch));
    }

    public static BranchSet binary(NodeId trueBranch, NodeId falseBranch) {
        return new BranchSet(Map.of("true", trueBranch, "false", falseBranch), Optional.ofNullable(falseBranch));
    }

    public Optional<NodeId> select(String outcome) {
        NodeId match = branches.get(outcome);
        if (match != null) {
            return Optional.of(match);
        }
        return defaultBranch;
    }
}
