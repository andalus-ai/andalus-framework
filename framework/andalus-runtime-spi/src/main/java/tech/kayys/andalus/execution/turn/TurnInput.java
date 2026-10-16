package tech.kayys.andalus.execution.turn;

import java.util.Map;
import java.util.Objects;

public record TurnInput(
        String text,
        Map<String, Object> data
) {

    public TurnInput {
        text = text == null ? "" : text;

        data = data == null
                ? Map.of()
                : Map.copyOf(data);
    }

    public static TurnInput text(String text) {
        return new TurnInput(text, Map.of());
    }

    public static TurnInput data(Map<String, Object> data) {
        return new TurnInput("", data);
    }

    public boolean hasText() {
        return !text.isBlank();
    }

    public boolean hasData() {
        return !data.isEmpty();
    }
}
