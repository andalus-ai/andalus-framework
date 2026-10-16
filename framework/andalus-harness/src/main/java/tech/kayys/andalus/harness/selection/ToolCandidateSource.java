package tech.kayys.andalus.harness.selection;

import tech.kayys.andalus.harness.protocol.ToolCatalogView;
import tech.kayys.andalus.tool.ToolDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Discovers candidate tools from the decision context's {@link ToolCatalogView} and/or registered tools.
 */
public class ToolCandidateSource implements CandidateSource {

    private final ConcurrentHashMap<String, CandidateDescriptor> staticTools = new ConcurrentHashMap<>();

    public ToolCandidateSource() {}

    public ToolCandidateSource(Collection<CandidateDescriptor> initialTools) {
        if (initialTools != null) {
            for (CandidateDescriptor t : initialTools) {
                registerTool(t);
            }
        }
    }

    public void registerTool(CandidateDescriptor tool) {
        if (tool != null) {
            staticTools.put(tool.id(), tool);
        }
    }

    public void registerTool(ToolDescriptor descriptor) {
        if (descriptor != null) {
            CandidateDescriptor cd = fromToolDescriptor(descriptor);
            staticTools.put(cd.id(), cd);
        }
    }

    @Override
    public SelectionTarget supportedTarget() {
        return SelectionTarget.TOOL;
    }

    @Override
    public CompletionStage<List<CandidateDescriptor>> discover(SelectionRequest request) {
        List<CandidateDescriptor> candidates = new ArrayList<>();

        // 1. Inspect decision context tool catalog view
        ToolCatalogView catalogView = request.context().tools();
        if (catalogView != null) {
            Collection<ToolDescriptor> tools = catalogView.availableTools();
            if (tools != null) {
                for (ToolDescriptor td : tools) {
                    candidates.add(fromToolDescriptor(td));
                }
            }
        }

        // 2. Add static/registered tools that haven't already been added
        for (CandidateDescriptor staticTool : staticTools.values()) {
            boolean alreadyPresent = candidates.stream().anyMatch(c -> c.id().equals(staticTool.id()));
            if (!alreadyPresent) {
                candidates.add(staticTool);
            }
        }

        return CompletableFuture.completedFuture(candidates);
    }

    private CandidateDescriptor fromToolDescriptor(ToolDescriptor td) {
        return new CandidateDescriptor(
                td.name() != null ? td.name() : td.toolId(),
                SelectionTarget.TOOL,
                td.capabilityKeys() != null ? td.capabilityKeys() : java.util.Set.of(),
                td.description() != null ? td.description() : "",
                td.inputSchema() != null ? td.inputSchema() : java.util.Map.of(),
                true,
                java.util.Map.of("version", td.version() != null ? td.version() : "1.0.0")
        );
    }
}
