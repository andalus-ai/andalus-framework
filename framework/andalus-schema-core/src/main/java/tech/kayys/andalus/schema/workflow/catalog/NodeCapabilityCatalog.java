package tech.kayys.andalus.schema.workflow.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * Portable snapshot of node types available to a designer. Discovery,
 * authorization filtering, and provider lifecycle are runtime responsibilities.
 */
public class NodeCapabilityCatalog {

    public static final int CURRENT_SCHEMA_VERSION = 1;

    @JsonProperty("schemaVersion")
    private int schemaVersion = CURRENT_SCHEMA_VERSION;

    @JsonProperty("catalogVersion")
    private String catalogVersion;

    @JsonProperty("generatedAt")
    private String generatedAt;

    @JsonProperty("nodeTypes")
    private List<NodeTypeDescriptor> nodeTypes = new ArrayList<>();

    public NodeCapabilityCatalog() {
    }

    public int getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(int schemaVersion) {
        if (schemaVersion < 1) {
            throw new IllegalArgumentException("schemaVersion must be greater than zero");
        }
        this.schemaVersion = schemaVersion;
    }
    public String getCatalogVersion() { return catalogVersion; }
    public void setCatalogVersion(String catalogVersion) { this.catalogVersion = catalogVersion; }
    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    public List<NodeTypeDescriptor> getNodeTypes() { return nodeTypes; }
    public void setNodeTypes(List<NodeTypeDescriptor> nodeTypes) {
        this.nodeTypes = nodeTypes == null ? new ArrayList<>() : nodeTypes;
    }
}
