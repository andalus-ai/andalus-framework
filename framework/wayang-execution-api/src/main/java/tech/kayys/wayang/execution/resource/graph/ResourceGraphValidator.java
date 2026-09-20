package tech.kayys.wayang.execution.resource.graph;

/**
 * Validates resource graph integrity (e.g. acyclicity, presence of dependent resources).
 */
public interface ResourceGraphValidator {

    /**
     * Validate graph structure, returning true if valid or throwing/returning false.
     */
    boolean validate(ResourceGraph graph);
}
