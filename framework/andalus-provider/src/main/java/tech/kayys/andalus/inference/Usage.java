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


/**
 * Usage
 */
public record Usage(
    int promptTokens,
    int completionTokens,
    int totalTokens,
    double cost
) {
    public static Usage of(int promptTokens, int completionTokens) {
        return new Usage(promptTokens, completionTokens, 
            promptTokens + completionTokens, 0.0);
    }
}