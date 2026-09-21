package tech.kayys.andalus.event;
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
 * Message type for LLM events
 */
public interface EventMessage {
    String role();
    String content();
    Map<String, Object> metadata();
    default String getContent() { return content(); }
    default String getRole() { return role(); }
}
