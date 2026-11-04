package tech.kayys.andalus.reasoner;

/** Indicates that a registered reasoning provider failed during execution. */
public final class ReasoningFailureException extends RuntimeException {

    public ReasoningFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
