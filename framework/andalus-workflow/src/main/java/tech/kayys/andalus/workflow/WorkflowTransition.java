package tech.kayys.andalus.workflow;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.Objects;

/**
 * A transition between workflow steps.
 */
public record WorkflowTransition(
    String from,
    String to,
    String condition
) {
    public WorkflowTransition {
        Objects.requireNonNull(from, "from cannot be null");
        Objects.requireNonNull(to, "to cannot be null");
    }
}