package tech.kayys.andalus.agent.spi.skills;

import java.util.Map;

public record SkillResult(
        String skillId,
        String invocationId,
        Status status,
        String observation,
        boolean success,
        Map<String, Object> memoryUpdates,
        Map<String, Object> outputData,
        long durationMs,
        Throwable error) {

    public SkillResult {
        memoryUpdates = memoryUpdates != null ? Map.copyOf(memoryUpdates) : Map.of();
        outputData = outputData != null ? Map.copyOf(outputData) : Map.of();
    }

    public enum Status {
        SUCCESS, FAILURE, ERROR
    }

    public static SkillResult success(String skillId, String observation) {
        return builder()
                .skillId(skillId)
                .observation(observation)
                .status(Status.SUCCESS)
                .build();
    }

    public static SkillResult failure(String skillId, String observation) {
        return builder()
                .skillId(skillId)
                .observation(observation)
                .status(Status.FAILURE)
                .build();
    }

    public static SkillResult failure(String skillId, Throwable error) {
        return builder()
                .skillId(skillId)
                .error(error)
                .status(Status.ERROR)
                .build();
    }

    public boolean hasMemoryUpdates() {
        return !memoryUpdates.isEmpty();
    }

    public String getObservation() {
        return observation;
    }

    public boolean isSuccess() {
        return success;
    }

    public Map<String, Object> getMemoryUpdates() {
        return memoryUpdates;
    }

    public Map<String, Object> getOutputs() {
        return outputData;
    }

    public boolean hasOutputs() {
        return !outputData.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public <T> T getOutput(String key, Class<T> type) {
        Object val = outputData.get(key);
        return type.isInstance(val) ? (T) val : null;
    }

    public Throwable getError() {
        return error;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String skillId;
        private String invocationId;
        private Status status = Status.SUCCESS;
        private String observation;
        private final java.util.Map<String, Object> memoryUpdates = new java.util.HashMap<>();
        private final java.util.Map<String, Object> outputData = new java.util.HashMap<>();
        private long durationMs;
        private Throwable error;

        public Builder skillId(String skillId) { this.skillId = skillId; return this; }
        public Builder invocationId(String invocationId) { this.invocationId = invocationId; return this; }
        public Builder status(Status status) { this.status = status; return this; }
        public Builder observation(String observation) { this.observation = observation; return this; }
        public Builder memoryUpdate(String key, Object value) { this.memoryUpdates.put(key, value); return this; }
        public Builder output(String key, Object value) { this.outputData.put(key, value); return this; }
        public Builder durationMs(long durationMs) { this.durationMs = durationMs; return this; }
        public Builder error(Throwable error) { this.error = error; return this; }

        public SkillResult build() {
            boolean success = status == Status.SUCCESS;
            return new SkillResult(skillId, invocationId, status, observation, success,
                    memoryUpdates, outputData, durationMs, error);
        }
    }
}
