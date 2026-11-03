package tech.kayys.andalus.schema.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import tech.kayys.andalus.schema.canvas.CanvasData;

import java.util.HashMap;
import java.util.Map;

/**
 * Versioned document exchanged by visual workflow editors and the Andalus
 * workflow APIs.
 *
 * <p>The executable graph remains owned by {@link WorkflowSpec}. Visual-only
 * state remains owned by {@link CanvasData}. This envelope deliberately does
 * not define execution or persistence behavior; a runtime may use
 * {@code revision} as an optimistic-concurrency token when storing drafts.</p>
 *
 * <p>This is a mutable Jackson-friendly schema type. Callers should not share
 * an instance across threads while it is being modified.</p>
 */
public class WorkflowDocument {

    public static final int CURRENT_SCHEMA_VERSION = 1;

    @JsonProperty("schemaVersion")
    private int schemaVersion = CURRENT_SCHEMA_VERSION;

    @JsonProperty("documentId")
    private String documentId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("revision")
    private long revision;

    @JsonProperty("workflow")
    private WorkflowSpec workflow;

    @JsonProperty("canvas")
    private CanvasData canvas;

    @JsonProperty("metadata")
    private Map<String, Object> metadata = new HashMap<>();

    public WorkflowDocument() {
        // Default constructor for JSON deserialization.
    }

    public WorkflowDocument(String documentId, String name, WorkflowSpec workflow, CanvasData canvas) {
        this.documentId = documentId;
        this.name = name;
        this.workflow = workflow;
        this.canvas = canvas;
    }

    public int getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(int schemaVersion) {
        if (schemaVersion < 1) {
            throw new IllegalArgumentException("schemaVersion must be greater than zero");
        }
        this.schemaVersion = schemaVersion;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the document revision. Revision zero represents a new,
     * not-yet-persisted document.
     */
    public long getRevision() {
        return revision;
    }

    /**
     * Sets the optimistic-concurrency revision supplied by the owning
     * persistence/runtime layer.
     */
    public void setRevision(long revision) {
        if (revision < 0) {
            throw new IllegalArgumentException("revision must not be negative");
        }
        this.revision = revision;
    }

    public WorkflowSpec getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkflowSpec workflow) {
        this.workflow = workflow;
    }

    public CanvasData getCanvas() {
        return canvas;
    }

    public void setCanvas(CanvasData canvas) {
        this.canvas = canvas;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata == null ? new HashMap<>() : metadata;
    }
}
