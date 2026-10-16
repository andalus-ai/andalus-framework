package tech.kayys.andalus.spi.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class AgentContractValidator {

    private AgentContractValidator() {
    }

    public static ValidationResult validate(
            AgentContract contract) {

        if (contract == null) {
            return ValidationResult.invalid(
                    "Agent contract cannot be null");
        }

        List<String> errors = new ArrayList<>();

        if (contract.identity() == null) {
            errors.add("Agent identity is required");
        }

        if (contract.version() == null) {
            errors.add("Agent contract version is required");
        }

        if (contract.capabilities() == null) {
            errors.add("Agent capabilities are required");
        }

        if (contract.input() == null) {
            errors.add("Input contract is required");
        }

        if (contract.output() == null) {
            errors.add("Output contract is required");
        }

        return errors.isEmpty()
                ? ValidationResult.success()
                : ValidationResult.invalid(errors);
    }

    public record ValidationResult(
            boolean valid,
            List<String> errors
    ) {

        public ValidationResult {
            errors = errors == null
                    ? List.of()
                    : List.copyOf(errors);
        }

        public static ValidationResult success() {
            return new ValidationResult(
                    true,
                    List.of());
        }

        public static ValidationResult invalid(
                String error) {

            Objects.requireNonNull(error);

            return new ValidationResult(
                    false,
                    List.of(error));
        }

        public static ValidationResult invalid(
                List<String> errors) {

            return new ValidationResult(
                    false,
                    errors);
        }
    }
}
