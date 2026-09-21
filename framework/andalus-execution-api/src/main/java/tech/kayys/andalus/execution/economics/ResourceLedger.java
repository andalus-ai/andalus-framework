package tech.kayys.andalus.execution.economics;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.resource.model.ResourceScope;
import tech.kayys.andalus.execution.resource.model.ResourceType;

import java.util.List;

/**
 * Ledger recording resource debits and credits across the execution fabric.
 */
public interface ResourceLedger {

    void recordUsage(ResourceUsage usage);

    ResourceBalance balance(ResourceScope scope, ResourceType type);

    List<ResourceUsage> history(ExecutionIdentity executionId);
}
