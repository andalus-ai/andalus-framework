package tech.kayys.wayang.spi.operator;

public record OperatorPermission(
        String id,
        String description
) {
    public OperatorPermission {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
    }

    public String name() {
        return id();
    }
}
