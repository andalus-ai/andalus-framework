package tech.kayys.andalus.harness.coordination;

/**
 * Raised when a Gamelan dispatch would be bound to a second Andalus execution, or an
 * execution to a second dispatch (6.17.2.17).
 *
 * <p>The binding is never overwritten: overwriting could orphan a real durable
 * execution. Conflicts must surface to reconciliation policy instead.</p>
 */
public class BindingConflictException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    private final String dispatchId;
    private final String existingExecutionId;
    private final String candidateExecutionId;

    public BindingConflictException(String dispatchId,
                                    String existingExecutionId,
                                    String candidateExecutionId) {
        super("Gamelan dispatch " + dispatchId + " is already bound to execution "
                + existingExecutionId + "; refusing to rebind to " + candidateExecutionId);
        this.dispatchId = dispatchId;
        this.existingExecutionId = existingExecutionId;
        this.candidateExecutionId = candidateExecutionId;
    }

    public String dispatchId() {
        return dispatchId;
    }

    public String existingExecutionId() {
        return existingExecutionId;
    }

    public String candidateExecutionId() {
        return candidateExecutionId;
    }
}
