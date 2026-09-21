package tech.kayys.andalus.spi.plugin;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.List;

import tech.kayys.andalus.descriptor.ParameterDescriptor;

/**
 * Extension Point - describes an extension point
 */
public record ExtensionPoint(
    String id,
    String name,
    String description,
    Class<?> interfaceType,
    boolean singleton,
    List<ParameterDescriptor> parameters
) {
    public static ExtensionPoint of(String id, String name, Class<?> interfaceType) {
        return new ExtensionPoint(id, name, null, interfaceType, false, List.of());
    }
}