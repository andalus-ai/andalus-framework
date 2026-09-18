package tech.kayys.wayang.execution.governance.limits;

public final class LimitExceededException extends RuntimeException {

    private final String limitId;

    public LimitExceededException(
            String limitId,
            String reason) {

        super(
                "Governance limit exceeded: " +
                limitId +
                (reason == null || reason.isBlank()
                        ? ""
                        : " - " + reason)
        );

        this.limitId = limitId;
    }

    public String limitId() {
        return limitId;
    }
}
