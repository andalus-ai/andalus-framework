package tech.kayys.andalus.state.session;

/**
 * Monotonically increasing version of an agent session state.
 */
public record SessionVersion(long value) {

    public static SessionVersion initial() {
        return new SessionVersion(1L);
    }

    public static SessionVersion of(long value) {
        return new SessionVersion(value);
    }

    public SessionVersion next() {
        return new SessionVersion(value + 1L);
    }
}
