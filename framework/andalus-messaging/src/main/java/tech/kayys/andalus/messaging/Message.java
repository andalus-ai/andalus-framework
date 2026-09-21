package tech.kayys.andalus.messaging;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import tech.kayys.andalus.extension.Id;

/**
 * Message
 */
public record Message(
    String id,
    String key,
    Object payload,
    Map<String, Object> headers,
    Instant timestamp
) {
    public static Message of(Object payload) {
        return new Message(
            Id.random().asString(),
            null,
            payload,
            Map.of(),
            Instant.now()
        );
    }
    
    public static Message of(String key, Object payload) {
        return new Message(
            Id.random().asString(),
            key,
            payload,
            Map.of(),
            Instant.now()
        );
    }
    
    public Message withHeader(String key, Object value) {
        Map<String, Object> newHeaders = new HashMap<>(headers);
        newHeaders.put(key, value);
        return new Message(id, this.key, payload, newHeaders, timestamp);
    }
}