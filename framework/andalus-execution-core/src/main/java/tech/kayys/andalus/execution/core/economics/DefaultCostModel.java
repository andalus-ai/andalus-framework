package tech.kayys.andalus.execution.core.economics;

import tech.kayys.andalus.execution.economics.Cost;
import tech.kayys.andalus.execution.economics.CostEstimate;
import tech.kayys.andalus.execution.economics.CostModel;
import tech.kayys.andalus.execution.economics.ResourceUsage;
import tech.kayys.andalus.execution.resource.requirement.ResourceRequirement;

import java.util.List;
import java.util.Set;

/**
 * Standard cost model calculating monetary unit costs based on resource consumption.
 */
public class DefaultCostModel implements CostModel {

    private final double unitPrice;
    private final String currency;

    public DefaultCostModel(double unitPrice, String currency) {
        this.unitPrice = unitPrice;
        this.currency = currency;
    }

    public DefaultCostModel() {
        this(0.002, "USD");
    }

    @Override
    public CostEstimate estimate(Set<ResourceRequirement> requirements) {
        int reqCount = (requirements != null && !requirements.isEmpty()) ? requirements.size() : 1;
        double min = reqCount * 0.01;
        double max = reqCount * 0.10;
        return CostEstimate.of(min, max, currency);
    }

    @Override
    public Cost calculate(List<ResourceUsage> usages) {
        if (usages == null || usages.isEmpty()) {
            return Cost.zero(currency);
        }

        double totalUnits = usages.stream()
            .mapToDouble(ResourceUsage::unitsConsumed)
            .sum();

        return Cost.of(totalUnits * unitPrice, currency);
    }
}
