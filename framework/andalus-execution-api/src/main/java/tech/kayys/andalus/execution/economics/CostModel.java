package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.util.List;
import java.util.Set;

/**
 * Economic model calculating and estimating resource costs.
 */
public interface CostModel {

    CostEstimate estimate(Set<ResourceRequirement> requirements);

    Cost calculate(List<ResourceUsage> usages);
}
