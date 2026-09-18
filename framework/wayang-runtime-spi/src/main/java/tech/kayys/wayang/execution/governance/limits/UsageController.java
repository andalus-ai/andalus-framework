package tech.kayys.wayang.execution.governance.limits;

public interface UsageController {

    LimitReservation reserve(
            LimitDefinition definition,
            LimitContext context,
            long estimatedAmount);
}
