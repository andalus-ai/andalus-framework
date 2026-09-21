package tech.kayys.andalus.input;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import tech.kayys.andalus.agent.AgentRequest;

/**
 * Input Stream Handler
 */
@FunctionalInterface
public interface InputStreamHandler {
    void onChunk(AgentRequest chunk) throws Exception;
}
