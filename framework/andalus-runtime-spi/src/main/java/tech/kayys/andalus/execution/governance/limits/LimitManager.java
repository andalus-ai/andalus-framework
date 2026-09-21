package tech.kayys.andalus.execution.governance.limits;

@FunctionalInterface
public interface LimitManager {

    LimitReservation acquire(LimitContext context);

    static LimitManager noop() {
        return context -> LimitReservation.noop();
    }
}
