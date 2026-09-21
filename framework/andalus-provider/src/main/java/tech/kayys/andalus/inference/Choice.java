package tech.kayys.andalus.inference;
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
 * Choice
 */
public record Choice(
    int index,
    Message message,
    String finishReason,
    Map<String, Object> metadata
) {
    public static Choice of(Message message) {
        return new Choice(0, message, "stop", Map.of());
    }
    
    public static Choice of(Message message, String finishReason) {
        return new Choice(0, message, finishReason, Map.of());
    }
}
