package tech.kayys.andalus.execution.runtime;

public record CheckpointHandle(String checkpointId, byte[] rawData) {}
