package tech.kayys.andalus.spi.capability;

public interface CapabilityRouter {

    CapabilityRoute route(CapabilityRoutingRequest request) throws CapabilityRoutingException;
}
