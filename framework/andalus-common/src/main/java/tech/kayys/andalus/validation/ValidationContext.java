package tech.kayys.andalus.validation;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.Map;

/**
 * Validation Context
 */
public interface ValidationContext {
    String getTenant();
    String getNamespace();
    Map<String, Object> getVariables();
    <T> T getVariable(String key, Class<T> type);
}
