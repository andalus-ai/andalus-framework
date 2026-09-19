package tech.kayys.wayang.execution.runtime;

public record CheckpointHandle(String checkpointId, byte[] rawData) {}
