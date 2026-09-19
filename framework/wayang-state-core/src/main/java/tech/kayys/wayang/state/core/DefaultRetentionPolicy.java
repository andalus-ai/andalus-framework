package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.retention.RetentionDecision;
import tech.kayys.wayang.state.retention.RetentionPolicy;
import tech.kayys.wayang.state.retention.RetentionStatus;
import tech.kayys.wayang.state.retention.StateOrArtifact;

import java.time.Duration;
import java.time.Instant;

public class DefaultRetentionPolicy implements RetentionPolicy {

    private final Duration hotDuration;
    private final Duration maxLifetime;

    public DefaultRetentionPolicy(Duration hotDuration, Duration maxLifetime) {
        this.hotDuration = hotDuration;
        this.maxLifetime = maxLifetime;
    }

    public static DefaultRetentionPolicy standard() {
        return new DefaultRetentionPolicy(Duration.ofDays(7), Duration.ofDays(90));
    }

    @Override
    public RetentionDecision evaluate(StateOrArtifact item) {
        Instant now = Instant.now();
        Duration age = Duration.between(item.createdAt(), now);

        if (age.compareTo(maxLifetime) > 0) {
            return RetentionDecision.expire();
        }
        if (age.compareTo(hotDuration) > 0) {
            return RetentionDecision.keep(RetentionStatus.WARM);
        }
        return RetentionDecision.keep(RetentionStatus.HOT);
    }
}
