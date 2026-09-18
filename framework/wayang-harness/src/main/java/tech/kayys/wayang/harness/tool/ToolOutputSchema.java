package tech.kayys.wayang.harness.tool;

import java.util.Map;

public interface ToolOutputSchema {

    SchemaType rootType();

    Map<String, SchemaProperty> properties();
}
