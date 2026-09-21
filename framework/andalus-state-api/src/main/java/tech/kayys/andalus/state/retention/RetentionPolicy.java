package tech.kayys.andalus.state.retention;

public interface RetentionPolicy {
    RetentionDecision evaluate(StateOrArtifact item);
}
