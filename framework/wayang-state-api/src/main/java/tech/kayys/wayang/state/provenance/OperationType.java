package tech.kayys.wayang.state.provenance;

public enum OperationType {
    MODEL_INFERENCE,
    TOOL_CALL,
    FILE_READ,
    FILE_WRITE,
    PROCESS_EXECUTION,
    ARTIFACT_TRANSFORM,
    CONTEXT_COMPACTION,
    HANDOFF,
    CHECKPOINT,
    CUSTOM
}
