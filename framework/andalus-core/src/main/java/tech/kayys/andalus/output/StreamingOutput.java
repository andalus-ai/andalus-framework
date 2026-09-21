package tech.kayys.andalus.spi.output;
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
 * Streaming Output
 */
public interface StreamingOutput {
    boolean hasNext();
    OutputResult next() throws Exception;
    void close() throws Exception;
    long getTotalBytes();
    long getProgress();
}

