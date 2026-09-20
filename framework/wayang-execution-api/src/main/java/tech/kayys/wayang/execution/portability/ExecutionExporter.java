package tech.kayys.wayang.execution.portability;

import tech.kayys.wayang.execution.mobility.ExecutionDurableState;
import tech.kayys.wayang.execution.mobility.ExecutionIdentity;
import tech.kayys.wayang.execution.provenance.ProvenanceChain;

/**
 * Exporter converting active execution state into a portable execution package.
 */
public interface ExecutionExporter {

    PortableExecution export(ExecutionIdentity executionId, ExecutionDurableState state, ProvenanceChain provenance);
}
