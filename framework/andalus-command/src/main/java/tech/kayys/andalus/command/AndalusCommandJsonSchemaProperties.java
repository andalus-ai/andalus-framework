package tech.kayys.andalus.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tech.kayys.andalus.client.AndalusJsonSchemaDocuments;


public final class AndalusCommandJsonSchemaProperties {

    private AndalusCommandJsonSchemaProperties() {
    }

    public static Map<String, Object> commandQueryProperty() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("surfaceId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("profileId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("resolvedSurfaceId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("category", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("commandId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("contractJsonSchemaId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("filtered", AndalusJsonSchemaDocuments.booleanProperty());
        return AndalusJsonSchemaDocuments.objectProperty(
                List.of(
                        "surfaceId",
                        "profileId",
                        "resolvedSurfaceId",
                        "category",
                        "commandId",
                        "contractJsonSchemaId",
                        "filtered"),
                true,
                properties);
    }

   public  static Map<String, Object> categorySummaryProperty() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("name", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("count", AndalusJsonSchemaDocuments.nonNegativeIntegerProperty());
        properties.put("commandIds", AndalusJsonSchemaDocuments.stringArrayProperty());
        return AndalusJsonSchemaDocuments.objectProperty(List.of("name", "count", "commandIds"), true, properties);
    }

    public static Map<String, Object> contractSummaryProperty() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("jsonSchemaId", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("schema", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("version", AndalusJsonSchemaDocuments.positiveIntegerProperty());
        properties.put("envelope", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("count", AndalusJsonSchemaDocuments.nonNegativeIntegerProperty());
        properties.put("commandIds", AndalusJsonSchemaDocuments.stringArrayProperty());
        return AndalusJsonSchemaDocuments.objectProperty(
                List.of("jsonSchemaId", "schema", "version", "envelope", "count", "commandIds"),
                true,
                properties);
    }

    public static Map<String, Object> commandProperty() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("title", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("command", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("category", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("description", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("surfaceIds", AndalusJsonSchemaDocuments.stringArrayProperty());
        properties.put("localOnly", AndalusJsonSchemaDocuments.booleanProperty());
        properties.put("contracts", AndalusJsonSchemaDocuments.arrayProperty(commandContractProperty()));
        return AndalusJsonSchemaDocuments.objectProperty(
                List.of("id", "title", "command", "category", "description", "surfaceIds", "localOnly"),
                true,
                properties);
    }

    private static Map<String, Object> commandContractProperty() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("schema", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("version", AndalusJsonSchemaDocuments.positiveIntegerProperty());
        properties.put("envelope", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("jsonSchemaId", AndalusJsonSchemaDocuments.stringProperty());
        return AndalusJsonSchemaDocuments.objectProperty(
                List.of("schema", "version", "envelope", "jsonSchemaId"),
                true,
                properties);
    }
}
