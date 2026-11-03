package tech.kayys.andalus.schema.workflow.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NodeCapabilityCatalogTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final NodeCapabilityCatalogValidator validator = new NodeCapabilityCatalogValidator();

    @Test
    void roundTripsCatalogDescriptorsAndPortSchemas() throws Exception {
        NodeTypeDescriptor descriptor = validDescriptor("agent.classify");
        descriptor.setConfigurationSchema(Map.of(
                "type", "object",
                "properties", Map.of("model", Map.of("type", "string"))));
        descriptor.setRequiredCapabilities(List.of("llm.chat"));
        descriptor.setRequiredPermissions(List.of("model.invoke"));
        descriptor.setSupportsStreaming(true);

        NodeCapabilityCatalog catalog = new NodeCapabilityCatalog();
        catalog.setCatalogVersion("2026.11");
        catalog.setNodeTypes(List.of(descriptor));

        NodeCapabilityCatalog restored = mapper.readValue(
                mapper.writeValueAsString(catalog), NodeCapabilityCatalog.class);

        assertThat(restored.getSchemaVersion()).isEqualTo(NodeCapabilityCatalog.CURRENT_SCHEMA_VERSION);
        assertThat(restored.getCatalogVersion()).isEqualTo("2026.11");
        assertThat(restored.getNodeTypes()).hasSize(1);
        assertThat(restored.getNodeTypes().get(0).getTypeId()).isEqualTo("agent.classify");
        assertThat(restored.getNodeTypes().get(0).getInputs()).singleElement()
                .satisfies(port -> {
                    assertThat(port.getDirection()).isEqualTo(NodePortDirection.INPUT);
                    assertThat(port.getDataType()).isEqualTo("prompt");
                });
        assertThat(restored.getNodeTypes().get(0).getConfigurationSchema())
                .containsEntry("type", "object");
        assertThat(restored.getNodeTypes().get(0).isSupportsStreaming()).isTrue();
    }

    @Test
    void validatesAWellFormedCatalog() {
        NodeCapabilityCatalog catalog = new NodeCapabilityCatalog();
        catalog.setNodeTypes(List.of(validDescriptor("agent.classify")));

        assertThat(validator.validate(catalog)).isEmpty();
    }

    @Test
    void rejectsDuplicateTypesAndInvalidPortDefinitions() {
        NodeTypeDescriptor first = validDescriptor("agent.classify");
        NodeTypeDescriptor duplicate = validDescriptor("agent.classify");
        duplicate.setInputs(List.of(
                new NodePortDescriptor("prompt", "Prompt", NodePortDirection.OUTPUT, "prompt"),
                new NodePortDescriptor("prompt", "Duplicate", NodePortDirection.INPUT, "prompt"),
                new NodePortDescriptor("missing-direction", "Broken", null, null)));

        NodeCapabilityCatalog catalog = new NodeCapabilityCatalog();
        catalog.setNodeTypes(List.of(first, duplicate));

        assertThat(validator.validate(catalog))
                .contains(
                        "nodeTypes[1].NODE_TYPE_ID_DUPLICATE",
                        "nodeTypes[1].inputs.[0].NODE_PORT_DIRECTION_INVALID",
                        "nodeTypes[1].inputs.[1].NODE_PORT_ID_DUPLICATE",
                        "nodeTypes[1].inputs.[2].NODE_PORT_DIRECTION_INVALID",
                        "nodeTypes[1].inputs.[2].NODE_PORT_DATA_TYPE_REQUIRED");
    }

    @Test
    void rejectsMissingCatalogAndUnsupportedSchemaVersion() {
        assertThat(validator.validate(null)).containsExactly("NODE_CATALOG_REQUIRED");

        NodeCapabilityCatalog catalog = new NodeCapabilityCatalog();
        catalog.setSchemaVersion(2);
        assertThat(validator.validate(catalog))
                .contains("NODE_CATALOG_SCHEMA_VERSION_UNSUPPORTED");
    }

    private NodeTypeDescriptor validDescriptor(String typeId) {
        NodeTypeDescriptor descriptor = new NodeTypeDescriptor();
        descriptor.setTypeId(typeId);
        descriptor.setVersion("1.0.0");
        descriptor.setDisplayName("Classify");
        descriptor.setCategory("agent");
        descriptor.setProviderId("andalus.core");
        descriptor.setInputs(List.of(
                new NodePortDescriptor("prompt", "Prompt", NodePortDirection.INPUT, "prompt")));
        descriptor.setOutputs(List.of(
                new NodePortDescriptor("result", "Result", NodePortDirection.OUTPUT, "json")));
        return descriptor;
    }
}
