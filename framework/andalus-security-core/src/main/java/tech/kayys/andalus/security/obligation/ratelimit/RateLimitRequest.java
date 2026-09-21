package tech.kayys.andalus.security.obligation.ratelimit;

public record RateLimitRequest(String key, int permits) {
    public static RateLimitRequest one(String key) {
        return new RateLimitRequest(key, 1);
    }
}
