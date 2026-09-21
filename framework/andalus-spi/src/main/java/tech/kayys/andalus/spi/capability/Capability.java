package tech.kayys.andalus.spi.capability;

/**
 * Universal capability interface in Andalus.
 *
 * <p>Represents an actionable functionality exposed by a plugin or the platform.</p>
 */
public interface Capability {

    /**
     * Unique identifier of this capability (e.g. "andalus.browser", "model.text").
     */
    String id();

    /**
     * Type classification of this capability.
     */
    CapabilityType type();

    /**
     * Metadata descriptor for this capability.
     */
    CapabilityDescriptor descriptor();
}
