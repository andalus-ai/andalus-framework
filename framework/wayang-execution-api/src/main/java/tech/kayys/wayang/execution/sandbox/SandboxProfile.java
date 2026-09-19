package tech.kayys.wayang.execution.sandbox;

/**
 * Predefined security and isolation profile factory.
 */
public interface SandboxProfile {

    String name();

    SandboxSpec resolve(SandboxContext context);

    String PROFILE_MINIMAL = "MINIMAL";
    String PROFILE_READ_ONLY = "READ_ONLY";
    String PROFILE_CODING = "CODING";
    String PROFILE_RESEARCH = "RESEARCH";
    String PROFILE_DATA_PROCESSING = "DATA_PROCESSING";
    String PROFILE_NETWORKED = "NETWORKED";
    String PROFILE_PRIVILEGED = "PRIVILEGED";
    String PROFILE_CUSTOM = "CUSTOM";
}
