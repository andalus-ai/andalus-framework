package tech.kayys.wayang.harness.authority;

/**
 * Fencing token ensuring only the active coordinator generation can mutate state.
 */
public interface FencingToken {

    long value();

    CoordinationEpoch epoch();

    boolean isAuthoritative(CoordinationEpoch currentEpoch);
}
