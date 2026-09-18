package tech.kayys.wayang.harness.tool;

import java.util.Map;

/**
 * Defines the contract for tool output schema operations in the Wayang framework.
 */


public interface ToolOutputSchema {

    SchemaType rootType();

    Map<String, SchemaProperty> properties();
}
