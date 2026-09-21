package tech.kayys.andalus.execution.economics;

/**
 * Controller enforcing admission criteria before an execution is scheduled onto the fabric.
 */
public interface AdmissionController {

    AdmissionDecision evaluate(AdmissionContext context);
}
