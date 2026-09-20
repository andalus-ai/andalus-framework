package tech.kayys.wayang.execution.portability;

import tech.kayys.wayang.execution.conformance.ConformanceProfile;

/**
 * Importer that assesses and hydrates a portable execution into a target runtime.
 */
public interface ExecutionImporter {

    ImportAssessment assess(PortableExecution execution, ConformanceProfile targetProfile);

    ImportResult importExecution(PortableExecution execution, ConformanceProfile targetProfile);
}
