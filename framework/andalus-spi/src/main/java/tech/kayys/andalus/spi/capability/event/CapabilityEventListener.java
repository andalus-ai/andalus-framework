package tech.kayys.andalus.spi.capability.event;

@FunctionalInterface
public interface CapabilityEventListener {

    void onEvent(CapabilityEvent event);
}
