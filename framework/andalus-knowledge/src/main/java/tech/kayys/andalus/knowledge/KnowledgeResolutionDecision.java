package tech.kayys.andalus.knowledge;

public record KnowledgeResolutionDecision(boolean allowed, String reason) {
    public static KnowledgeResolutionDecision allow() { return new KnowledgeResolutionDecision(true, ""); }
    public static KnowledgeResolutionDecision deny(String reason) {
        return new KnowledgeResolutionDecision(false, reason == null ? "Knowledge access denied" : reason);
    }
}
