package tech.kayys.andalus.registry;

import java.util.List;
import java.util.Map;

/**
 * Standard protocol definition.
 */
public record AndalusStandardDefinition(
        String standardId,
        String name,
        String version,
        String binding,
        String specUrl,
        List<String> aliases,
        Map<String, Object> attributes) {

    public AndalusStandardDefinition {
        standardId = SdkText.trimToDefault(standardId, "unknown");
        name = SdkText.trimToDefault(name, standardId);
        version = SdkText.trimToEmpty(version);
        binding = SdkText.trimToEmpty(binding);
        specUrl = SdkText.trimToEmpty(specUrl);
        aliases = aliases == null ? List.of() : List.copyOf(aliases);
        attributes = attributes == null ? Map.of() : SdkMaps.orderedCopy(attributes);
    }
}
