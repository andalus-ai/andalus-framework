package tech.kayys.andalus.harness.plan;

import java.util.List;

/**
 * High-level plan describing steps to be executed within the Harness.
 */
public interface ExecutionPlan {

    String id();

    List<ExecutionStep> steps();
}
