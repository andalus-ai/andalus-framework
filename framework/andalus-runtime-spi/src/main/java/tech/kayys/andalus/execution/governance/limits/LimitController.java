package tech.kayys.andalus.execution.governance.limits;

@FunctionalInterface
public interface LimitController {

    LimitReservation acquire(LimitContext context);
}
