package tech.kayys.andalus.plugin;

import tech.kayys.andalus.spi.capability.CapabilityAvailability;
import tech.kayys.andalus.spi.capability.CapabilityHealth;
import tech.kayys.andalus.spi.capability.CapabilityHealthChecker;
import tech.kayys.andalus.spi.capability.CapabilityProviderRegistration;
import tech.kayys.andalus.spi.capability.CapabilityProviderStatus;

import java.time.Instant;
import java.util.Map;

public final class DefaultCapabilityHealthChecker implements CapabilityHealthChecker {

    @Override
    public CapabilityProviderStatus check(CapabilityProviderRegistration provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider registration cannot be null");
        }

        return new CapabilityProviderStatus(
                provider.capabilityId(),
                provider.providerId(),
                CapabilityAvailability.AVAILABLE,
                CapabilityHealth.HEALTHY,
                Instant.now(),
                0L,
                0.0,
                "Healthy",
                Map.of()
        );
    }
}
