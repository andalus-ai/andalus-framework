package tech.kayys.andalus.schema.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.schema.canvas.CanvasData;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkflowDocumentTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void roundTripsExecutableWorkflowAndEditorCanvasSeparately() throws Exception {
        WorkflowSpec workflow = new WorkflowSpec();
        workflow.setEntryNodeId("start");

        CanvasData canvas = new CanvasData();
        canvas.layout.autoLayout = true;

        WorkflowDocument document = new WorkflowDocument("wf-123", "Support workflow", workflow, canvas);
        document.setDescription("Customer support automation");
        document.setRevision(4);
        document.setMetadata(Map.of("owner", "platform-team"));

        String json = objectMapper.writeValueAsString(document);
        WorkflowDocument restored = objectMapper.readValue(json, WorkflowDocument.class);

        assertThat(restored.getSchemaVersion()).isEqualTo(WorkflowDocument.CURRENT_SCHEMA_VERSION);
        assertThat(restored.getDocumentId()).isEqualTo("wf-123");
        assertThat(restored.getName()).isEqualTo("Support workflow");
        assertThat(restored.getDescription()).isEqualTo("Customer support automation");
        assertThat(restored.getRevision()).isEqualTo(4);
        assertThat(restored.getWorkflow().getEntryNodeId()).isEqualTo("start");
        assertThat(restored.getCanvas().layout.autoLayout).isTrue();
        assertThat(restored.getMetadata()).containsEntry("owner", "platform-team");
    }

    @Test
    void defaultsSchemaVersionRevisionAndMetadata() {
        WorkflowDocument document = new WorkflowDocument();

        assertThat(document.getSchemaVersion()).isEqualTo(1);
        assertThat(document.getRevision()).isZero();
        assertThat(document.getMetadata()).isEmpty();
    }

    @Test
    void rejectsInvalidSchemaVersionAndRevision() {
        WorkflowDocument document = new WorkflowDocument();

        assertThatThrownBy(() -> document.setSchemaVersion(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> document.setRevision(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void normalizesNullMetadataToAnEmptyMap() {
        WorkflowDocument document = new WorkflowDocument();
        document.setMetadata(null);

        assertThat(document.getMetadata()).isNotNull().isEmpty();
    }
}
