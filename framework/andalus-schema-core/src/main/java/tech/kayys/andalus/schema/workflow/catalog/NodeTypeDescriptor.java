package tech.kayys.andalus.schema.workflow.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Portable, non-executable description of a node type offered to workflow
 * designers. Provider implementations and runtime instances are never
 * serialized into this descriptor.
 */
public class NodeTypeDescriptor {

    @JsonProperty("typeId")
    private String typeId;

    @JsonProperty("version")
    private String version;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("description")
    private String description;

    /** Extensible category key, e.g. trigger, agent, tool, control, or data. */
    @JsonProperty("category")
    private String category;

    /** Stable UI asset key; clients resolve it through their own trusted assets. */
    @JsonProperty("icon")
    private String icon;

    @JsonProperty("providerId")
    private String providerId;

    @JsonProperty("pluginId")
    private String pluginId;

    @JsonProperty("inputs")
    private List<NodePortDescriptor> inputs = new ArrayList<>();

    @JsonProperty("outputs")
    private List<NodePortDescriptor> outputs = new ArrayList<>();

    /** JSON Schema object for the node's configuration. */
    @JsonProperty("configurationSchema")
    private Map<String, Object> configurationSchema = new HashMap<>();

    @JsonProperty("requiredCapabilities")
    private List<String> requiredCapabilities = new ArrayList<>();

    @JsonProperty("requiredPermissions")
    private List<String> requiredPermissions = new ArrayList<>();

    @JsonProperty("minimumFrameworkVersion")
    private String minimumFrameworkVersion;

    @JsonProperty("maximumFrameworkVersion")
    private String maximumFrameworkVersion;

    @JsonProperty("supportsStreaming")
    private boolean supportsStreaming;

    @JsonProperty("supportsRetries")
    private boolean supportsRetries;

    @JsonProperty("supportsHumanApproval")
    private boolean supportsHumanApproval;

    @JsonProperty("metadata")
    private Map<String, Object> metadata = new HashMap<>();

    public NodeTypeDescriptor() {
    }

    public String getTypeId() { return typeId; }
    public void setTypeId(String typeId) { this.typeId = typeId; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getPluginId() { return pluginId; }
    public void setPluginId(String pluginId) { this.pluginId = pluginId; }
    public List<NodePortDescriptor> getInputs() { return inputs; }
    public void setInputs(List<NodePortDescriptor> inputs) { this.inputs = inputs == null ? new ArrayList<>() : inputs; }
    public List<NodePortDescriptor> getOutputs() { return outputs; }
    public void setOutputs(List<NodePortDescriptor> outputs) { this.outputs = outputs == null ? new ArrayList<>() : outputs; }
    public Map<String, Object> getConfigurationSchema() { return configurationSchema; }
    public void setConfigurationSchema(Map<String, Object> schema) {
        this.configurationSchema = schema == null ? new HashMap<>() : schema;
    }
    public List<String> getRequiredCapabilities() { return requiredCapabilities; }
    public void setRequiredCapabilities(List<String> values) {
        this.requiredCapabilities = values == null ? new ArrayList<>() : values;
    }
    public List<String> getRequiredPermissions() { return requiredPermissions; }
    public void setRequiredPermissions(List<String> values) {
        this.requiredPermissions = values == null ? new ArrayList<>() : values;
    }
    public String getMinimumFrameworkVersion() { return minimumFrameworkVersion; }
    public void setMinimumFrameworkVersion(String value) { this.minimumFrameworkVersion = value; }
    public String getMaximumFrameworkVersion() { return maximumFrameworkVersion; }
    public void setMaximumFrameworkVersion(String value) { this.maximumFrameworkVersion = value; }
    public boolean isSupportsStreaming() { return supportsStreaming; }
    public void setSupportsStreaming(boolean value) { this.supportsStreaming = value; }
    public boolean isSupportsRetries() { return supportsRetries; }
    public void setSupportsRetries(boolean value) { this.supportsRetries = value; }
    public boolean isSupportsHumanApproval() { return supportsHumanApproval; }
    public void setSupportsHumanApproval(boolean value) { this.supportsHumanApproval = value; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata == null ? new HashMap<>() : metadata;
    }
}
