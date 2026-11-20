package tech.kayys.andalus.agent.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import tech.kayys.andalus.tool.CancellationToken;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * STDIO MCP client with one response reader and request-id based multiplexing.
 * Writes are serialized; callers wait independently and responses may arrive out of order.
 */
public final class StdioMcpToolClient implements McpToolClient {
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
    private static final Map<String, Object> DEFAULT_INPUT_SCHEMA = Map.of("type", "object");
    private final ObjectMapper mapper;
    private final Map<String, StdioProcessSession> activeSessions = new ConcurrentHashMap<>();

    public StdioMcpToolClient() { this(new ObjectMapper()); }
    public StdioMcpToolClient(ObjectMapper mapper) { this.mapper = Objects.requireNonNull(mapper, "mapper"); }

    @Override
    public Uni<List<McpToolDescriptor>> listTools(McpServerConfig server) {
        return Uni.createFrom().item(() -> {
            try {
                Map<String, Object> response = getOrCreateSession(server)
                    .sendRequest(jsonRpcRequest("tools/list", Map.of()), null);
                Object result = response.get("result");
                if (result instanceof Map<?, ?> map && map.get("tools") instanceof List<?> list) {
                    return list.stream().map(McpMaps::fromObject).filter(raw -> !raw.isEmpty())
                        .map(raw -> toToolDescriptor(server.id(), raw)).toList();
                }
                return List.<McpToolDescriptor>of();
            } catch (Exception e) {
                return List.<McpToolDescriptor>of();
            }
        });
    }

    @Override
    public Uni<McpToolCallResult> callTool(McpToolInvocation invocation) {
        return callTool(invocation, null);
    }

    @Override
    public Uni<McpToolCallResult> callTool(McpToolInvocation invocation, CancellationToken cancellation) {
        Instant start = Instant.now();
        return Uni.createFrom().item(() -> {
            try {
                if (cancellation != null && cancellation.isCancellationRequested())
                    throw new CancellationException("STDIO MCP call cancelled before dispatch.");
                String serverId = invocation.serverId();
                StdioProcessSession session = activeSessions.get(serverId);
                if (session == null) {
                    String command = (String) invocation.context().get(McpTransportContext.KEY_COMMAND);
                    @SuppressWarnings("unchecked")
                    List<String> args = (List<String>) invocation.context().getOrDefault(McpTransportContext.KEY_ARGS, List.of());
                    if (command != null && !command.isBlank())
                        session = getOrCreateSession(McpServerConfig.stdio(serverId, command, args));
                }
                if (session == null)
                    return McpToolCallResult.failure("No active stdio session for MCP server: " + serverId, elapsed(start));

                Map<String, Object> request = jsonRpcRequest("tools/call", Map.of(
                    "name", invocation.toolName(), "arguments", invocation.arguments()));
                Map<String, Object> response = session.sendRequest(request, cancellation);
                long duration = elapsed(start);
                if (response.containsKey("error"))
                    return McpToolCallResult.failure(String.valueOf(response.get("error")), duration);
                return McpToolCallResult.success(response.get("result"), duration,
                    Map.of("serverId", serverId, "transport", "stdio"));
            } catch (CancellationException e) {
                throw e;
            } catch (Exception e) {
                return McpToolCallResult.failure("Stdio MCP call failed: " + e.getMessage(), elapsed(start));
            }
        });
    }

    @Override
    public Uni<Void> disconnect(String serverId) {
        return Uni.createFrom().item(() -> {
            StdioProcessSession session = activeSessions.remove(serverId);
            if (session != null) session.close();
            return null;
        });
    }

    private synchronized StdioProcessSession getOrCreateSession(McpServerConfig server) throws IOException {
        StdioProcessSession existing = activeSessions.get(server.id());
        if (existing != null && existing.isAlive()) return existing;
        List<String> command = new ArrayList<>();
        command.add(server.command());
        command.addAll(server.args());
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectError(ProcessBuilder.Redirect.DISCARD);
        StdioProcessSession session = new StdioProcessSession(builder.start(), mapper);
        try {
            Map<String, Object> init = Map.of(
                "protocolVersion", "2024-11-05", "capabilities", Map.of("tools", Map.of()),
                "clientInfo", Map.of("name", "AndalusAgent", "version", "1.0.0"));
            session.sendRequest(jsonRpcRequest("initialize", init), null);
            session.sendNotification("notifications/initialized", Map.of());
        } catch (Exception e) {
            session.close();
            throw new IOException("Failed to initialize stdio MCP server: " + e.getMessage(), e);
        }
        activeSessions.put(server.id(), session);
        return session;
    }

    private McpToolDescriptor toToolDescriptor(String serverId, Map<String, Object> raw) {
        String name = String.valueOf(raw.getOrDefault("name", "unknown"));
        String description = String.valueOf(raw.getOrDefault("description", name));
        Map<String, Object> schema = McpMaps.fromObject(raw.get("inputSchema"));
        if (schema.isEmpty()) schema = DEFAULT_INPUT_SCHEMA;
        return new McpToolDescriptor(serverId, name, description, schema, Map.of("transport", "stdio"));
    }

    private Map<String, Object> jsonRpcRequest(String method, Map<String, Object> params) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("jsonrpc", "2.0");
        request.put("id", UUID.randomUUID().toString());
        request.put("method", method);
        if (params != null && !params.isEmpty()) request.put("params", params);
        return request;
    }

    private static long elapsed(Instant start) { return Duration.between(start, Instant.now()).toMillis(); }

    private static final class StdioProcessSession implements AutoCloseable {
        private final Process process;
        private final ObjectMapper mapper;
        private final BufferedWriter writer;
        private final BufferedReader reader;
        private final Map<String, CompletableFuture<Map<String, Object>>> pending = new ConcurrentHashMap<>();
        private final Thread readerThread;
        private volatile IOException terminalFailure;
        private volatile boolean closed;

        StdioProcessSession(Process process, ObjectMapper mapper) {
            this.process = process;
            this.mapper = mapper;
            writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            readerThread = Thread.ofVirtual().name("andalus-mcp-stdio-reader").start(this::readResponses);
        }

        boolean isAlive() { return !closed && process.isAlive() && terminalFailure == null; }

        Map<String, Object> sendRequest(Map<String, Object> request, CancellationToken cancellation) throws Exception {
            if (cancellation != null && cancellation.isCancellationRequested())
                throw new CancellationException("STDIO MCP request cancelled before dispatch.");
            Object requestId = request.get("id");
            if (requestId == null) throw new IllegalArgumentException("JSON-RPC request must include an id.");
            String id = String.valueOf(requestId);
            CompletableFuture<Map<String, Object>> response = new CompletableFuture<>();
            if (!isAlive()) throw unavailableFailure();
            if (pending.putIfAbsent(id, response) != null)
                throw new IllegalStateException("Duplicate JSON-RPC request id: " + id);

            CancellationToken.CancellationRegistration registration = cancellation == null ? () -> { } :
                cancellation.onCancel(() -> {
                    if (pending.remove(id, response)) {
                        response.completeExceptionally(new CancellationException("STDIO MCP request cancelled."));
                        try {
                            sendNotification("notifications/cancelled",
                                Map.of("requestId", requestId, "reason", "Execution cancelled"));
                        } catch (IOException ignored) { }
                    }
                });
            try {
                if (cancellation != null && cancellation.isCancellationRequested())
                    throw new CancellationException("STDIO MCP request cancelled before write.");
                writeMessage(request);
                return response.get();
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof Exception exception) throw exception;
                throw new IOException("STDIO MCP request failed", cause);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (pending.remove(id, response)) {
                    response.completeExceptionally(new CancellationException("STDIO MCP caller interrupted."));
                    try {
                        sendNotification("notifications/cancelled",
                            Map.of("requestId", requestId, "reason", "Caller interrupted"));
                    } catch (IOException ignored) { }
                }
                throw e;
            } finally {
                registration.close();
                pending.remove(id, response);
            }
        }

        void sendNotification(String method, Map<String, Object> params) throws IOException {
            Map<String, Object> notification = new LinkedHashMap<>();
            notification.put("jsonrpc", "2.0");
            notification.put("method", method);
            if (params != null && !params.isEmpty()) notification.put("params", params);
            writeMessage(notification);
        }

        private void writeMessage(Map<String, Object> message) throws IOException {
            synchronized (writer) {
                if (closed || !process.isAlive()) throw unavailableFailure();
                writer.write(mapper.writeValueAsString(message));
                writer.newLine();
                writer.flush();
            }
        }

        private void readResponses() {
            try {
                String line;
                while (!closed && (line = reader.readLine()) != null) {
                    Map<String, Object> message = mapper.readValue(line, MAP_TYPE);
                    Object responseId = message.get("id");
                    if (responseId == null) continue;
                    CompletableFuture<Map<String, Object>> future = pending.remove(String.valueOf(responseId));
                    if (future != null) future.complete(message);
                }
                if (!closed) failPending(new IOException("EOF received from stdio MCP process."));
            } catch (IOException e) {
                if (!closed) failPending(e);
            } finally {
                try { reader.close(); } catch (IOException ignored) { }
            }
        }

        private void failPending(IOException failure) {
            terminalFailure = failure;
            pending.forEach((id, future) -> {
                if (pending.remove(id, future)) future.completeExceptionally(failure);
            });
        }

        private IOException unavailableFailure() {
            return terminalFailure != null ? terminalFailure : new IOException("STDIO MCP process session is closed.");
        }

        @Override
        public void close() {
            if (closed) return;
            closed = true;
            failPending(new IOException("STDIO MCP process session closed."));
            try { writer.close(); } catch (IOException ignored) { }
            try { reader.close(); } catch (IOException ignored) { }
            process.destroyForcibly();
        }
    }
}
