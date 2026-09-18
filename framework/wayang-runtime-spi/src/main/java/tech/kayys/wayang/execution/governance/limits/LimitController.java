package tech.kayys.wayang.execution.governance.limits;

@FunctionalInterface
public interface LimitController {

    LimitReservation acquire(LimitContext context);
}
