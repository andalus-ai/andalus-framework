package tech.kayys.wayang.tool.schema;

public record DefaultToolOutputSchema(
        SchemaType type,
        long maxSizeBytes,
        boolean truncateOnOverflow
) implements ToolOutputSchema {

    public static DefaultToolOutputSchema text() {
        return new DefaultToolOutputSchema(SchemaType.STRING, 1_000_000L, true);
    }

    public static DefaultToolOutputSchema json(long maxSizeBytes) {
        return new DefaultToolOutputSchema(SchemaType.OBJECT, maxSizeBytes, false);
    }
}
