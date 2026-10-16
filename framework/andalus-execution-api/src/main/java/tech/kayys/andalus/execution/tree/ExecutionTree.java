package tech.kayys.andalus.execution.tree;

import java.util.List;
import java.util.Optional;

/**
 * Read-only view of an agent execution lineage tree.
 *
 * <p>This abstraction does not define workflow semantics. It only describes
 * parent/child relationships between executions.</p>
 */
public interface ExecutionTree {

    /**
     * Returns the root execution.
     */
    ExecutionNode root();

    /**
     * Returns an execution node by ID.
     */
    Optional<ExecutionNode> find(String executionId);

    /**
     * Returns direct children of an execution.
     */
    List<ExecutionNode> children(String executionId);

    /**
     * Returns the direct parent, if any.
     */
    default Optional<ExecutionNode> parent(String executionId) {
        return find(executionId)
                .flatMap(ExecutionNode::parentExecutionId)
                .flatMap(this::find);
    }

    /**
     * Returns all nodes belonging to this execution tree.
     */
    List<ExecutionNode> nodes();

    /**
     * Returns true when the supplied execution belongs to this tree.
     */
    default boolean contains(String executionId) {
        return find(executionId).isPresent();
    }
}
