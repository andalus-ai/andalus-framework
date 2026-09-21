package tech.kayys.andalus.json.handler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import tech.kayys.andalus.json.JsonSchemaProperty;
import tech.kayys.andalus.json.SchemaHandler;

public class ReadinessSchemaHandler implements SchemaHandler {

    private static final String DOMAIN_ID = "readiness";
    private static final String DESCRIPTION = "Andalus readiness report schema";

    @Override
    public String getDomainId() {
        return DOMAIN_ID;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public Map<String, Object> buildSchema() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("title", "Readiness Schema");
        schema.put("description", DESCRIPTION);
        schema.put("properties", new LinkedHashMap<>());
        schema.put("required", List.of("readinessId", "ready", "exitCode", "issueCount", "probes", "issues", "attributes"));
        return schema;
    }

    @Override
    public Map<String, JsonSchemaProperty> buildProperties() {
        Map<String, JsonSchemaProperty> properties = new LinkedHashMap<>();
        return properties;
    }
}
