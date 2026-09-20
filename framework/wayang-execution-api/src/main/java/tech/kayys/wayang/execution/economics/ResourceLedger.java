package tech.kayys.wayang.execution.economics;

import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.resource.model.ResourceScope;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.List;

/**
 * Ledger recording resource debits and credits across the execution fabric.
 */
public interface ResourceLedger {

    void recordUsage(ResourceUsage usage);

    ResourceBalance balance(ResourceScope scope, ResourceType type);

    List<ResourceUsage> history(ExecutionIdentity executionId);
}
