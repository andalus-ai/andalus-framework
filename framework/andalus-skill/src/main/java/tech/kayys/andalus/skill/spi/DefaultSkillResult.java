package tech.kayys.andalus.skill.spi;

import java.util.Map;

public record DefaultSkillResult(
        String skillId,
        String invocationId,
        boolean success,
        String observation,
        Map<String, Object> memoryUpdates,
        Map<String, Object> outputs,
        long durationMs,
        Throwable error
) implements SkillResult {

    public DefaultSkillResult {
        skillId = skillId == null ? "" : skillId;
        invocationId = invocationId == null ? "" : invocationId;
        observation = observation == null ? "" : observation;
        memoryUpdates = memoryUpdates != null ? Map.copyOf(memoryUpdates) : Map.of();
        outputs = outputs != null ? Map.copyOf(outputs) : Map.of();
    }

    @Override
    public Map<String, Object> getOutputs() {
        return outputs;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    @Override
    public String getInvocationId() {
        return invocationId;
    }

    @Override
    public String getObservation() {
        return observation;
    }

    @Override
    public Map<String, Object> getMemoryUpdates() {
        return memoryUpdates;
    }

    @Override
    public Throwable getError() {
        return error;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String skillId = "";
        private String invocationId = "";
        private boolean success = true;
        private String observation = "";
        private Map<String, Object> memoryUpdates = Map.of();
        private Map<String, Object> outputs = Map.of();
        private long durationMs = 0L;
        private Throwable error;

        public Builder skillId(String skillId) {
            this.skillId = skillId;
            return this;
        }

        public Builder invocationId(String invocationId) {
            this.invocationId = invocationId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder observation(String observation) {
            this.observation = observation;
            return this;
        }

        public Builder memoryUpdates(Map<String, Object> memoryUpdates) {
            this.memoryUpdates = memoryUpdates;
            return this;
        }

        public Builder outputs(Map<String, Object> outputs) {
            this.outputs = outputs;
            return this;
        }

        public Builder durationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder error(Throwable error) {
            this.error = error;
            return this;
        }

        public DefaultSkillResult build() {
            return new DefaultSkillResult(
                    skillId,
                    invocationId,
                    success,
                    observation,
                    memoryUpdates,
                    outputs,
                    durationMs,
                    error
            );
        }
    }
}
