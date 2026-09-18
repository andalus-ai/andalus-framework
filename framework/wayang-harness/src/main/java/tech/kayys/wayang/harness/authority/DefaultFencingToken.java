package tech.kayys.wayang.harness.authority;

import java.util.Objects;

/**
 * Immutable reference record implementing {@link FencingToken}.
 */
public record DefaultFencingToken(
        long value,
        CoordinationEpoch epoch
) implements FencingToken {

    public DefaultFencingToken {
        Objects.requireNonNull(epoch, "CoordinationEpoch cannot be null");
    }

    public static DefaultFencingToken of(long value, CoordinationEpoch epoch) {
        return new DefaultFencingToken(value, epoch);
    }

    @Override
    public boolean isAuthoritative(CoordinationEpoch currentEpoch) {
        if (currentEpoch == null) return false;
        return this.epoch.value() == currentEpoch.value();
    }
}
