package tech.kayys.andalus.knowledge;

public record KnowledgeSourceFailure(String sourceId, String code, String message) {
    public static final String CODE_NOT_FOUND = "NOT_FOUND";
    public static final String CODE_UNHEALTHY = "UNHEALTHY";
    public static final String CODE_POLICY_DENIED = "POLICY_DENIED";
    public static final String CODE_QUERY_FAILED = "QUERY_FAILED";
    public static final String CODE_TIMEOUT = "TIMEOUT";
}
