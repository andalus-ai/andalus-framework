package tech.kayys.wayang.execution.governance.limits;

public interface LimitReservation extends AutoCloseable {

    void commit();

    void rollback();

    boolean active();

    @Override
    default void close() {
        rollback();
    }

    static LimitReservation noop() {
        return new LimitReservation() {
            @Override
            public void commit() {}

            @Override
            public void rollback() {}

            @Override
            public boolean active() {
                return false;
            }
        };
    }
}
