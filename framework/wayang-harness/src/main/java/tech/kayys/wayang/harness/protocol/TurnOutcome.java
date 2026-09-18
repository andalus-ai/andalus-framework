package tech.kayys.wayang.harness.protocol;

import java.util.Optional;

/**
 * Represents a turn outcome.
 *
 * <p>Its components capture `successful`, `result`, `error`.</p>
 *
 * @param successful the successful
 * @param result the result
 * @param error the error
 */


public record TurnOutcome(
        boolean successful,
        Object result,
        Optional<String> error
) {
    public TurnOutcome {
        if (error == null) error = Optional.empty();
    }

    public static TurnOutcome success(Object result) {
        return new TurnOutcome(true, result, Optional.empty());
    }

    public static TurnOutcome failure(String error) {
        return new TurnOutcome(false, null, Optional.ofNullable(error));
    }
}
