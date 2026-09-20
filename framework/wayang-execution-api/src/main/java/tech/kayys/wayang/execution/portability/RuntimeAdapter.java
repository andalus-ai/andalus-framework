package tech.kayys.wayang.execution.portability;

import tech.kayys.wayang.execution.conformance.ConformanceProfile;

/**
 * Adapter SPI allowing different runtime infrastructures to execute portable packages.
 */
public interface RuntimeAdapter {

    RuntimeIdentity identity();

    ConformanceProfile profile();

    ImportResult execute(PortableExecution execution);
}
