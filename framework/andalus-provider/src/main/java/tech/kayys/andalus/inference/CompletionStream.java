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


import java.util.Iterator;

/**
 * Completion Stream
 */
public interface CompletionStream extends Iterator<CompletionResult> {
    void close() throws Exception;
    boolean isComplete();
    String getStreamId();
}