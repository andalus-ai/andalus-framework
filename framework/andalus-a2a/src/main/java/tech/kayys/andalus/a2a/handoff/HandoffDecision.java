package tech.kayys.andalus.a2a.handoff;

public record HandoffDecision(boolean allowed, String reason) {
    public static HandoffDecision allow() { return new HandoffDecision(true, ""); }
    public static HandoffDecision deny(String reason) {
        return new HandoffDecision(false, reason == null ? "Handoff denied" : reason);
    }
}
