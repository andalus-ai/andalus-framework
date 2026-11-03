package tech.kayys.andalus.schema.workflow.catalog;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

/** Portable description of a typed input or output port on a workflow node. */
public class NodePortDescriptor {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("direction")
    private NodePortDirection direction;

    /** Logical data type such as string, json, message, or a domain type URI. */
    @JsonProperty("dataType")
    private String dataType;

    @JsonProperty("required")
    private boolean required;

    /** Optional JSON Schema fragment describing values accepted/emitted by this port. */
    @JsonProperty("schema")
    private Map<String, Object> schema = new HashMap<>();

    public NodePortDescriptor() {
    }

    public NodePortDescriptor(String id, String name, NodePortDirection direction, String dataType) {
        this.id = id;
        this.name = name;
        this.direction = direction;
        this.dataType = dataType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public NodePortDirection getDirection() { return direction; }
    public void setDirection(NodePortDirection direction) { this.direction = direction; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
    public Map<String, Object> getSchema() { return schema; }
    public void setSchema(Map<String, Object> schema) {
        this.schema = schema == null ? new HashMap<>() : schema;
    }
}
