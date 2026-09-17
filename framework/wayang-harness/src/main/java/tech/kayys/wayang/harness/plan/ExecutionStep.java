package tech.kayys.wayang.harness.plan;

import java.util.Map;

/**
 * An individual unit of execution within an {@link ExecutionPlan}.
 */
public interface ExecutionStep {

    String id();

    String type();

    Map<String, Object> parameters();
}
