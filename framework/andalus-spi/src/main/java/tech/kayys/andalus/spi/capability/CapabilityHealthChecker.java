package tech.kayys.andalus.spi.capability;

public interface CapabilityHealthChecker {

    CapabilityProviderStatus check(CapabilityProviderRegistration provider);
}
