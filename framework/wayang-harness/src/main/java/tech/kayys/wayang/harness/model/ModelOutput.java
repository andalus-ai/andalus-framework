package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.tool.ToolIntent;

import java.util.List;
import java.util.Optional;

public record ModelOutput(
        String text,
        List<ToolIntent> toolIntents,
        Optional<Object> structuredData,
        boolean finishReasonStop
) {
    public ModelOutput {
        toolIntents = toolIntents != null ? List.copyOf(toolIntents) : List.of();
        if (structuredData == null) structuredData = Optional.empty();
    }

    public boolean hasToolIntents() {
        return !toolIntents.isEmpty();
    }

    public static ModelOutput text(String text) {
        return new ModelOutput(text, List.of(), Optional.empty(), true);
    }

    public static ModelOutput withTools(String text, List<ToolIntent> toolIntents) {
        return new ModelOutput(text, toolIntents, Optional.empty(), false);
    }
}
