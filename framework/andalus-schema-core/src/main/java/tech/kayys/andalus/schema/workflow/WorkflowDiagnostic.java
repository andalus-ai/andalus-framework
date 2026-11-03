package tech.kayys.andalus.schema.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Machine-readable validation finding associated with a workflow document. */
public class WorkflowDiagnostic {

    @JsonProperty("code")
    private String code;

    @JsonProperty("severity")
    private WorkflowDiagnosticSeverity severity;

    @JsonProperty("pointer")
    private String pointer;

    @JsonProperty("message")
    private String message;

    public WorkflowDiagnostic() {
    }

    public WorkflowDiagnostic(String code, WorkflowDiagnosticSeverity severity, String pointer, String message) {
        this.code = code;
        this.severity = severity;
        this.pointer = pointer;
        this.message = message;
    }

    public String getCode() { return code; }
    public WorkflowDiagnosticSeverity getSeverity() { return severity; }
    public String getPointer() { return pointer; }
    public String getMessage() { return message; }

    public void setCode(String code) { this.code = code; }
    public void setSeverity(WorkflowDiagnosticSeverity severity) { this.severity = severity; }
    public void setPointer(String pointer) { this.pointer = pointer; }
    public void setMessage(String message) { this.message = message; }
}
