package tech.kayys.andalus.core;

import java.util.ArrayList;
import java.util.List;

public final class AgentDefinitionValidator {

    private AgentDefinitionValidator() {
    }

    public static Result validate(
            AgentDefinition definition) {

        if (definition == null) {
            return Result.invalid(
                    "AgentDefinition cannot be null");
        }

        var errors = new ArrayList<String>();

        if (definition.identity() == null) {
            errors.add("Agent identity is required");
        }

        if (definition.role() == null || definition.role().isBlank()) {
            errors.add("Agent role is required");
        }

        if ((definition.goal() == null || definition.goal().isBlank())
                && (definition.instructions() == null || definition.instructions().isBlank())) {

            errors.add(
                    "Agent goal or instructions must be provided");
        }

        for (var capability :
                definition.capabilities()) {

            if (capability == null
                    || capability.isBlank()) {

                errors.add(
                        "Capability identifiers cannot be blank");
            }
        }

        for (var skill : definition.skills()) {
            if (skill == null) {
                errors.add("Skill reference cannot be null");
            }
        }

        for (var tool : definition.tools()) {
            if (tool == null) {
                errors.add("Tool reference cannot be null");
            }
        }

        return errors.isEmpty()
                ? Result.success()
                : Result.invalid(errors);
    }

    public record Result(
            boolean valid,
            List<String> errors) {

        public Result {
            errors = errors == null
                    ? List.of()
                    : List.copyOf(errors);
        }

        public static Result success() {
            return new Result(true, List.of());
        }

        public static Result invalid(
                String error) {

            return new Result(
                    false,
                    List.of(error));
        }

        public static Result invalid(
                List<String> errors) {

            return new Result(false, errors);
        }
    }
}
