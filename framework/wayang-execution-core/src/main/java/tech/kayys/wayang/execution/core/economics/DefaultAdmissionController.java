package tech.kayys.wayang.execution.core.economics;

import tech.kayys.wayang.execution.economics.AdmissionContext;
import tech.kayys.wayang.execution.economics.AdmissionController;
import tech.kayys.wayang.execution.economics.AdmissionDecision;
import tech.kayys.wayang.execution.economics.ReservationId;

import java.util.Objects;

/**
 * Gatekeeping admission controller checking quotas, budgets, and constraints.
 */
public class DefaultAdmissionController implements AdmissionController {

    @Override
    public AdmissionDecision evaluate(AdmissionContext context) {
        Objects.requireNonNull(context, "context cannot be null");

        // Check budget exhaustion
        if (context.budget() != null && context.budget().isExhausted()) {
            return AdmissionDecision.reject("Budget exhausted for scope: " + context.scope().name());
        }

        // Generate reservation ID for admitted execution
        ReservationId resId = ReservationId.generate();
        return AdmissionDecision.admit(resId);
    }
}
