package tech.kayys.andalus.spi.sandbox;

public record ResourceLimitStatus(
        boolean cpuEnforced,
        boolean memoryEnforced,
        boolean diskEnforced,
        boolean processEnforced,
        boolean timeoutEnforced,
        boolean outputEnforced,
        boolean fileCountEnforced
) {
}
