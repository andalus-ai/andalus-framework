package tech.kayys.andalus.agent.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.tool.CancellationToken;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StdioMcpToolClientMultiplexingTest {

    @Test
    void correlatesConcurrentOutOfOrderResponsesAndKeepsSessionAfterPerCallCancellation() throws Exception {
        StdioMcpToolClient client = new StdioMcpToolClient();
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classpath = System.getProperty("java.class.path");
        McpServerConfig server = McpServerConfig.stdio("test", java,
            List.of("-cp", classpath, FakeMcpStdioServer.class.getName()));

        List<McpToolDescriptor> tools = client.listTools(server).await().atMost(Duration.ofSeconds(5));
        assertThat(tools).extracting(McpToolDescriptor::name).containsExactly("echo");

        McpToolDescriptor tool = tools.getFirst();
        CompletableFuture<McpToolCallResult> slow = CompletableFuture.supplyAsync(() ->
            client.callTool(McpToolInvocation.of(tool, Map.of("label", "slow", "delayMs", 250)))
                .await().atMost(Duration.ofSeconds(5)));
        CompletableFuture<McpToolCallResult> fast = CompletableFuture.supplyAsync(() ->
            client.callTool(McpToolInvocation.of(tool, Map.of("label", "fast", "delayMs", 10)))
                .await().atMost(Duration.ofSeconds(5)));

        assertThat(fast.get(5, TimeUnit.SECONDS).text()).contains("fast");
        assertThat(slow.get(5, TimeUnit.SECONDS).text()).contains("slow");

        CancellationToken cancellation = CancellationToken.create();
        CompletableFuture<McpToolCallResult> cancelled = CompletableFuture.supplyAsync(() ->
            client.callTool(McpToolInvocation.of(tool, Map.of("label", "cancel-me", "delayMs", 1_000)), cancellation)
                .await().atMost(Duration.ofSeconds(5)));
        Thread.sleep(75);
        cancellation.cancel();
        assertThatThrownBy(() -> cancelled.get(5, TimeUnit.SECONDS))
            .hasCauseInstanceOf(java.util.concurrent.CancellationException.class);

        McpToolCallResult afterCancellation = client.callTool(
            McpToolInvocation.of(tool, Map.of("label", "still-alive", "delayMs", 1)))
            .await().atMost(Duration.ofSeconds(5));
        assertThat(afterCancellation.text()).contains("still-alive");
        client.disconnect("test").await().atMost(Duration.ofSeconds(2));
    }

    /** Child process used as a deterministic newline-delimited JSON-RPC MCP server. */
    public static final class FakeMcpStdioServer {
        public static void main(String[] args) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            String line;
            while ((line = System.in.readLine()) != null) {
                Map<String, Object> request = mapper.readValue(line,
                    new com.fasterxml.jackson.core.type.TypeReference<>() {});
                Object id = request.get("id");
                String method = String.valueOf(request.get("method"));
                if (id == null) continue;
                if ("initialize".equals(method)) {
                    write(mapper, Map.of("jsonrpc", "2.0", "id", id,
                        "result", Map.of("protocolVersion", "2024-11-05", "capabilities", Map.of(),
                            "serverInfo", Map.of("name", "test", "version", "1"))));
                } else if ("tools/list".equals(method)) {
                    write(mapper, Map.of("jsonrpc", "2.0", "id", id,
                        "result", Map.of("tools", List.of(Map.of("name", "echo", "description", "Echo",
                            "inputSchema", Map.of("type", "object"))))));
                } else if ("tools/call".equals(method)) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> params = (Map<String, Object>) request.get("params");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
                    Thread worker = new Thread(() -> {
                        try {
                            Thread.sleep(((Number) arguments.getOrDefault("delayMs", 0)).longValue());
                            write(mapper, Map.of("jsonrpc", "2.0", "id", id,
                                "result", Map.of("content", List.of(Map.of("type", "text",
                                    "text", String.valueOf(arguments.get("label")))))));
                        } catch (Exception ignored) { }
                    });
                    worker.setDaemon(true);
                    worker.start();
                }
            }
        }

        private static synchronized void write(ObjectMapper mapper, Map<String, Object> response) throws Exception {
            System.out.println(mapper.writeValueAsString(response));
            System.out.flush();
        }
    }
}
