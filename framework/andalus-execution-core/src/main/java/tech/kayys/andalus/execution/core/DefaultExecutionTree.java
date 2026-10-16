package tech.kayys.andalus.execution.core;

import tech.kayys.andalus.execution.tree.ExecutionNode;
import tech.kayys.andalus.execution.tree.ExecutionTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Thread-safe execution lineage tree.
 */
public final class DefaultExecutionTree implements ExecutionTree {

    private final Map<String, ExecutionNode> nodes =
            Collections.synchronizedMap(new LinkedHashMap<>());

    private final String rootExecutionId;

    public DefaultExecutionTree(ExecutionNode root) {
        Objects.requireNonNull(root, "root must not be null");

        if (!root.isRoot()) {
            throw new IllegalArgumentException(
                    "Root execution node cannot have a parent");
        }

        this.rootExecutionId = root.executionId();
        nodes.put(root.executionId(), root);
    }

    /**
     * Registers an execution node.
     *
     * <p>The parent must already exist in this tree.</p>
     */
    public synchronized void add(ExecutionNode node) {
        Objects.requireNonNull(node, "node must not be null");

        if (nodes.containsKey(node.executionId())) {
            throw new IllegalArgumentException(
                    "Execution already exists: " + node.executionId());
        }

        if (node.parentExecutionId().isPresent()) {
            String parentId = node.parentExecutionId().get();

            if (!nodes.containsKey(parentId)) {
                throw new IllegalArgumentException(
                        "Parent execution does not exist: " + parentId);
            }
        }

        nodes.put(node.executionId(), node);
    }

    @Override
    public ExecutionNode root() {
        return nodes.get(rootExecutionId);
    }

    @Override
    public Optional<ExecutionNode> find(String executionId) {
        if (executionId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(nodes.get(executionId));
    }

    @Override
    public List<ExecutionNode> children(String executionId) {
        if (executionId == null) {
            return List.of();
        }

        synchronized (nodes) {
            return nodes.values()
                    .stream()
                    .filter(node ->
                            node.parentExecutionId()
                                    .map(executionId::equals)
                                    .orElse(false))
                    .toList();
        }
    }

    @Override
    public List<ExecutionNode> nodes() {
        synchronized (nodes) {
            return List.copyOf(new ArrayList<>(nodes.values()));
        }
    }
}
