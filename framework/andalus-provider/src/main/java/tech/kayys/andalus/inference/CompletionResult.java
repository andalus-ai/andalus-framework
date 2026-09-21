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


import java.util.List;
import java.util.Map;

import tech.kayys.andalus.extension.Id;


/**
 * Completion Result - complete result model
 */
public record CompletionResult(
    String id,
    String model,
    List<Choice> choices,
    Usage usage,
    Map<String, Object> metadata,
    long generationTimeMs
) {
    public static CompletionResult of(Choice choice) {
        return new CompletionResult(
            Id.random().asString(),
            null,
            List.of(choice),
            null,
            Map.of(),
            0
        );
    }
    
    public static CompletionResult of(String content) {
        return new CompletionResult(
            Id.random().asString(),
            null,
            List.of(Choice.of(Message.assistant(content))),
            null,
            Map.of(),
            0
        );
    }
    
    public String getContent() {
        return choices.isEmpty() ? null : choices.get(0).message().content();
    }
    
    public Message getFirstMessage() {
        return choices.isEmpty() ? null : choices.get(0).message();
    }
}
