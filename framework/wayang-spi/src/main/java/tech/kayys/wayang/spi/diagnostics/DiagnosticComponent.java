package tech.kayys.wayang.spi.diagnostics;

public record DiagnosticComponent(
        DiagnosticComponentType type,
        String id) {

    public DiagnosticComponent {
        if (type == null) {
            throw new IllegalArgumentException(
                    "type must not be null");
        }

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "id must not be blank");
        }
    }

    public static DiagnosticComponent of(DiagnosticComponentType type, String id) {
        return new DiagnosticComponent(type, id);
    }
}
