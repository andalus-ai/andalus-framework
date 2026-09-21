package tech.kayys.andalus.execution.portability;

import tech.kayys.andalus.execution.conformance.ConformanceProfile;

/**
 * Adapter SPI allowing different runtime infrastructures to execute portable packages.
 */
public interface RuntimeAdapter {

    RuntimeIdentity identity();

    ConformanceProfile profile();

    ImportResult execute(PortableExecution execution);
}
