package tech.kayys.andalus.execution.portability;

import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import tech.kayys.andalus.execution.provenance.ProvenanceChain;

/**
 * Exporter converting active execution state into a portable execution package.
 */
public interface ExecutionExporter {

    PortableExecution export(ExecutionIdentity executionId, ExecutionDurableState state, ProvenanceChain provenance);
}
