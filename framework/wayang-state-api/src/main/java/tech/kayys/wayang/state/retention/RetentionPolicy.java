package tech.kayys.wayang.state.retention;

public interface RetentionPolicy {
    RetentionDecision evaluate(StateOrArtifact item);
}
