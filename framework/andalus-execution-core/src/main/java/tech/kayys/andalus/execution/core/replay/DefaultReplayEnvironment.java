package tech.kayys.andalus.execution.core.replay;

import tech.kayys.andalus.execution.replay.ClockProvider;
import tech.kayys.andalus.execution.replay.EffectSandbox;
import tech.kayys.andalus.execution.replay.RandomProvider;
import tech.kayys.andalus.execution.replay.ReplayEnvironment;

import java.util.Objects;

/**
 * Standard implementation of {@link ReplayEnvironment}.
 */
public class DefaultReplayEnvironment implements ReplayEnvironment {

    private final String id;
    private final ClockProvider clock;
    private final RandomProvider random;
    private final EffectSandbox effects;

    public DefaultReplayEnvironment(String id, ClockProvider clock, RandomProvider random, EffectSandbox effects) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
        this.random = Objects.requireNonNull(random, "random cannot be null");
        this.effects = Objects.requireNonNull(effects, "effects cannot be null");
    }

    public static DefaultReplayEnvironment standard(String id) {
        return new DefaultReplayEnvironment(id, new FixedClockProvider(), new DeterministicRandomProvider(), new DefaultEffectSandbox());
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ClockProvider clock() {
        return clock;
    }

    @Override
    public RandomProvider random() {
        return random;
    }

    @Override
    public EffectSandbox effects() {
        return effects;
    }
}
