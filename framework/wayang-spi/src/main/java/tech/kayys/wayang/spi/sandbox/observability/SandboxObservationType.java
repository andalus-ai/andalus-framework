package tech.kayys.wayang.spi.sandbox.observability;

public enum SandboxObservationType {

    CREATED,

    STARTING,

    STARTED,

    STOPPING,

    STOPPED,

    DESTROYING,

    DESTROYED,

    FAILED,

    RESOURCE_SAMPLE,

    ARTIFACT_UPLOAD,

    ARTIFACT_DOWNLOAD,

    ARTIFACT_DELETE,

    NETWORK_SAMPLE,

    HEALTH_CHANGED
}
