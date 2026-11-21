package tech.kayys.andalus.agent.mcp;

import io.smallrye.mutiny.Uni;
import tech.kayys.andalus.tool.CancellationToken;

import java.util.List;
import java.util.Map;

public interface McpToolClient {

    Uni<List<McpToolDescriptor>> listTools(McpServerConfig server);

    Uni<McpToolCallResult> callTool(McpToolInvocation invocation);

    default Uni<McpToolCallResult> callTool(McpToolInvocation invocation, CancellationToken cancellation) {
        if (cancellation != null && cancellation.isCancellationRequested()) return Uni.createFrom().failure(new java.util.concurrent.CancellationException("MCP tool call cancelled before dispatch."));
        Uni<McpToolCallResult> result = callTool(invocation);
        if (cancellation == null) return result;
        return result.map(value -> {
            if (cancellation.isCancellationRequested()) throw new java.util.concurrent.CancellationException("MCP tool call cancelled.");
            return value;
        });
    }

    default Uni<McpToolCallResult> callTool(McpToolDescriptor tool, Map<String, Object> arguments) {
        return callTool(McpToolInvocation.of(tool, arguments));
    }

    default Uni<Void> disconnect(String serverId) {
        return Uni.createFrom().voidItem();
    }
}
