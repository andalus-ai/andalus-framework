package tech.kayys.wayang.agent.plan;

import tech.kayys.wayang.agent.WayangAgentListener;
import tech.kayys.wayang.agent.react.BaseReActAgent;
import tech.kayys.wayang.provider.ChatMessage;
import tech.kayys.wayang.provider.StreamEvent;
import tech.kayys.wayang.provider.ToolSpec;
import tech.kayys.wayang.tool.Tool;
import tech.kayys.wayang.tool.ToolInvocation;
import tech.kayys.wayang.tool.ToolResult;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Plan-and-Solve Agent (§35) — three-phase loop: Plan → Solve → Reflect.
 *
 * <ol>
 *   <li><b>Plan</b>   — model decomposes the task into ordered sub-steps.</li>
 *   <li><b>Solve</b>  — for each step, runs a ReAct-style tool loop.</li>
 *   <li><b>Reflect</b>— model critiques the assembled answer; re-solves if needed (up to 2 rounds).</li>
 * </ol>
 *
 * <p>Adheres to "An Agent decides. The Execution Kernel executes." — it never manages
 * state beyond its conversation history.</p>
 */
public class PlanAndSolveAgent extends BaseReActAgent {

    private static final int MAX_STEPS_PER_SUBPLAN = 10;
    private static final int MAX_REFLECTION_ROUNDS  = 2;

    private static final String PLANNER_SUFFIX =
        "\n\nYou are in PLANNING mode. Decompose the request into a numbered list of " +
        "atomic sub-steps an AI can execute with available tools. Do NOT execute yet.\n" +
        "Format:\nPLAN:\n1. <step>\n2. <step>\n...";

    private static final String SOLVER_PREFIX =
        "\n\nYou are in SOLVING mode. Execute the plan step-by-step using tools when needed. " +
        "After all steps report: ALL STEPS DONE.\nPlan:\n";

    private static final String REFLECTOR_SUFFIX =
        "\n\nYou are in REFLECTION mode. If the answer fully satisfies the request output " +
        "\"REFLECTION: SATISFIED\". Otherwise output \"REFLECTION: NEEDS IMPROVEMENT\" " +
        "followed by what is missing, then an improved answer.";

    @Override
    public void send(String userInput, WayangAgentListener listener) {
        if (memory != null) memory.addMessage(ChatMessage.userText(userInput));

        listener.onTextDelta("\n[PlanAndSolve] Phase 1: Planning\n");
        String plan = runPhase(basePrompt() + PLANNER_SUFFIX, listener, "Planning");
        if (plan == null) return;

        listener.onTextDelta("\n[PlanAndSolve] Phase 2: Solving\n");
        String solution = runSolvingPhase(userInput, plan, listener, 0);
        if (solution == null) return;

        listener.onTextDelta("\n[PlanAndSolve] Phase 3: Reflecting\n");
        runReflectionPhase(userInput, solution, listener, 0);

        listener.onDone("stop");
    }

    // -------------------------------------------------------------------------

    private String runPhase(String systemPrompt, WayangAgentListener listener, String phaseName) {
        StringBuilder buf = new StringBuilder();
        try {
            provider.streamChat(currentHistory(), systemPrompt, List.of(), 0.3, 2048, event -> {
                if (event instanceof StreamEvent.TextDelta td) {
                    listener.onTextDelta(td.text());
                    buf.append(td.text());
                }
            });
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            listener.onError(phaseName + " phase failed: " + e.getMessage());
            return null;
        }
        String text = buf.toString().trim();
        if (memory != null) memory.addMessage(ChatMessage.assistantText(text));
        return text;
    }

    private String runSolvingPhase(String userInput, String plan, WayangAgentListener listener, int stepCount) {
        if (stepCount >= MAX_STEPS_PER_SUBPLAN) {
            listener.onError("Max solving steps exceeded.");
            return null;
        }
        StringBuilder buf = new StringBuilder();
        AtomicReference<String> pendingTool = new AtomicReference<>();
        try {
            provider.streamChat(currentHistory(), basePrompt() + SOLVER_PREFIX + plan,
                buildToolSpecs(), 0.5, 4096, event -> {
                if (event instanceof StreamEvent.TextDelta td) {
                    listener.onTextDelta(td.text());
                    buf.append(td.text());
                } else if (event instanceof StreamEvent.ToolUseStart ts) {
                    listener.onToolCallStart(ts.id(), ts.name());
                    pendingTool.set(ts.name());
                } else if (event instanceof StreamEvent.ToolUseEnd te) {
                    invokeTool(te, pendingTool.get(), listener);
                }
            });
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            listener.onError("Solving phase failed: " + e.getMessage());
            return null;
        }
        String solution = buf.toString().trim();
        if (memory != null) memory.addMessage(ChatMessage.assistantText(solution));
        return solution;
    }

    private void runReflectionPhase(String userInput, String solution, WayangAgentListener listener, int round) {
        if (round >= MAX_REFLECTION_ROUNDS) return;
        String reflection = runPhase(basePrompt() + REFLECTOR_SUFFIX, listener, "Reflection");
        if (reflection == null) return;
        if (reflection.contains("REFLECTION: NEEDS IMPROVEMENT")) {
            listener.onTextDelta("\n[PlanAndSolve] Re-solving based on reflection\n");
            String improved = runSolvingPhase(userInput, reflection, listener, 0);
            if (improved != null) runReflectionPhase(userInput, improved, listener, round + 1);
        }
    }

    // -------------------------------------------------------------------------

    private void invokeTool(StreamEvent.ToolUseEnd toolEnd, String toolName, WayangAgentListener listener) {
        if (toolName == null) return;
        Tool tool = tools.stream().filter(t -> t.descriptor().name().equals(toolName))
            .findFirst().orElse(null);
        if (tool == null) { listener.onError("Tool not found: " + toolName); return; }
        Map<String, Object> args = toolEnd.input().asStringObjectMap();
        ToolInvocation inv = buildInvocation(toolName, args);
        try {
            ToolResult result = toolExecutor != null
                ? toolExecutor.executeViaPolicy(inv, () -> safeDirect(tool, inv))
                : safeDirect(tool, inv);
            listener.onToolResult(toolEnd.id(), toolName, result);
            if (memory != null) {
                String out = result.getOutputs() != null ? result.getOutputs().toString() : "";
                memory.addMessage(ChatMessage.toolResults(List.of(
                    (tech.kayys.wayang.resource.ContentPart.ToolResult)
                        tech.kayys.wayang.resource.ContentPart.toolResult(toolEnd.id(), out, !result.isSuccess()))));
            }
        } catch (Exception e) {
            listener.onError("Tool [" + toolName + "] error: " + e.getMessage());
        }
    }

    private ToolResult safeDirect(Tool tool, ToolInvocation inv) {
        try { return tool.execute(inv, null).get(); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    private List<ChatMessage> currentHistory() {
        return memory != null ? memory.getHistory() : Collections.emptyList();
    }

    private String basePrompt() {
        return systemPrompt != null ? systemPrompt : "You are a helpful AI assistant.";
    }

    private List<ToolSpec> buildToolSpecs() {
        return tools.stream()
            .map(t -> new ToolSpec(t.descriptor().name(), t.descriptor().description(), t.descriptor().inputSchema()))
            .collect(Collectors.toList());
    }

    private ToolInvocation buildInvocation(String name, Map<String, Object> args) {
        return new ToolInvocation() {
            public String name() { return name; }
            public Map<String, Object> arguments() { return args; }
            public tech.kayys.wayang.identity.ResourceId id() { return null; }
            public tech.kayys.wayang.extension.Metadata metadata() {
                return tech.kayys.wayang.extension.Metadata.builder().build();
            }
            public tech.kayys.wayang.resource.ResourceType type() {
                return new tech.kayys.wayang.resource.ResourceType.Custom("invocation");
            }
        };
    }
}
