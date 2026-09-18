package tech.kayys.wayang.spi.operator;

import java.util.Map;

public sealed interface OperatorResult<T>
        permits OperatorResult.Success,
                OperatorResult.Failure {

    record Success<T>(
            T value,
            Map<String, Object> metadata
    ) implements OperatorResult<T> {

        public Success {
            metadata = metadata == null
                    ? Map.of()
                    : Map.copyOf(metadata);
        }
    }

    record Failure<T>(
            String code,
            String message,
            Map<String, Object> metadata
    ) implements OperatorResult<T> {

        public Failure {
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("code must not be blank");
            }

            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException(
                        "message must not be blank");
            }

            metadata = metadata == null
                    ? Map.of()
                    : Map.copyOf(metadata);
        }
    }

    static <T> Success<T> success(T value) {
        return new Success<>(value, Map.of());
    }

    static <T> Success<T> success(
            T value,
            Map<String, Object> metadata) {

        return new Success<>(value, metadata);
    }

    static <T> Failure<T> failure(
            String code,
            String message) {

        return new Failure<>(code, message, Map.of());
    }

    static <T> Failure<T> failure(
            String code,
            String message,
            Map<String, Object> metadata) {

        return new Failure<>(code, message, metadata);
    }
}
