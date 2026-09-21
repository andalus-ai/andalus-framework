package tech.kayys.andalus.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tech.kayys.andalus.client.AndalusJsonSchemaDocuments;
import tech.kayys.andalus.contract.AndalusContractDescriptor;
import tech.kayys.andalus.contract.AndalusContractJsonSchema;

public  final class AndalusCommandDiscoveryJsonSchema {

    private AndalusCommandDiscoveryJsonSchema() {
    }

    public static boolean matches(AndalusContractDescriptor contract) {
        return AndalusCommandDiscoveryContract.SCHEMA.equals(contract.schema())
                && AndalusCommandDiscoveryContract.COMMANDS_DISCOVERY.equals(contract.envelope());
    }

    public static AndalusContractJsonSchema schema(AndalusContractDescriptor contract) {
        return AndalusJsonSchemaDocuments.envelopeSchema(
                contract,
                "Andalus command discovery envelope",
                required(),
                properties());
    }

    private static List<String> required() {
        return List.of(
                "product",
                "surfaceId",
                "profileId",
                "resolvedSurfaceId",
                "category",
                "commandId",
                "contractJsonSchemaId",
                "query",
                "totalCommands",
                "matchingCommands",
                "categories",
                "categoryCounts",
                "categorySummaries",
                "contractJsonSchemaIds",
                "contractJsonSchemaIdCounts",
                "contractSummaries",
                "commandIds");
    }

    private static Map<String, Object> properties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("product", AndalusJsonSchemaDocuments.stringProperty());
        properties.put("surfaceId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("profileId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("resolvedSurfaceId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("category", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("commandId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("contractJsonSchemaId", AndalusJsonSchemaDocuments.nullableStringProperty());
        properties.put("query", AndalusCommandJsonSchemaProperties.commandQueryProperty());
        properties.put("totalCommands", AndalusJsonSchemaDocuments.nonNegativeIntegerProperty());
        properties.put("matchingCommands", AndalusJsonSchemaDocuments.nonNegativeIntegerProperty());
        properties.put("categories", AndalusJsonSchemaDocuments.stringArrayProperty());
        properties.put("categoryCounts", AndalusJsonSchemaDocuments.countMapProperty());
        properties.put("categorySummaries", AndalusJsonSchemaDocuments.arrayProperty(
                AndalusCommandJsonSchemaProperties.categorySummaryProperty()));
        properties.put("contractJsonSchemaIds", AndalusJsonSchemaDocuments.stringArrayProperty());
        properties.put("contractJsonSchemaIdCounts", AndalusJsonSchemaDocuments.countMapProperty());
        properties.put("contractSummaries", AndalusJsonSchemaDocuments.arrayProperty(
                AndalusCommandJsonSchemaProperties.contractSummaryProperty()));
        properties.put("commandIds", AndalusJsonSchemaDocuments.stringArrayProperty());
        properties.put("commands", AndalusJsonSchemaDocuments.arrayProperty(
                AndalusCommandJsonSchemaProperties.commandProperty()));
        return properties;
    }
}
