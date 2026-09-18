package tech.kayys.wayang.tool.schema;

/**
 * Contract defining output constraints for a tool.
 */
public interface ToolOutputSchema {

    SchemaType type();

    long maxSizeBytes();

    boolean truncateOnOverflow();
}
