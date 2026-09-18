package tech.kayys.wayang.execution.governance.limits;

import java.util.List;
import java.util.Objects;

public final class DefaultLimitManager implements LimitManager {

    private final List<LimitController> controllers;

    public DefaultLimitManager(List<? extends LimitController> controllers) {
        Objects.requireNonNull(
                controllers,
                "controllers cannot be null"
        );

        this.controllers = List.copyOf(controllers);
    }

    @Override
    public LimitReservation acquire(LimitContext context) {
        Objects.requireNonNull(
                context,
                "context cannot be null"
        );

        LimitReservationGroup group = new LimitReservationGroup();

        try {
            for (LimitController controller : controllers) {
                LimitReservation reservation = controller.acquire(context);
                group.add(reservation);
            }

            return group;
        } catch (RuntimeException exception) {
            group.rollback();
            throw exception;
        }
    }
}
