package tech.kayys.andalus.spi.operator.plugin;

import tech.kayys.andalus.spi.plugin.PluginState;

import java.util.List;

public record PluginSummary(
        String id,
        String name,
        String version,
        String description,
        PluginState state,
        List<String> extensionTypes
) {
    public PluginSummary {
        extensionTypes =
                extensionTypes == null
                        ? List.of()
                        : List.copyOf(extensionTypes);
    }
}
