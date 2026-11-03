package tech.kayys.andalus.schema.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Immutable result returned by the structural workflow document validator. */
public class WorkflowValidationResult {

    private final List<WorkflowDiagnostic> diagnostics;

    public WorkflowValidationResult(List<WorkflowDiagnostic> diagnostics) {
        this.diagnostics = diagnostics == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(diagnostics));
    }

    public List<WorkflowDiagnostic> getDiagnostics() {
        return diagnostics;
    }

    public boolean isValid() {
        return diagnostics.stream().noneMatch(
                diagnostic -> diagnostic.getSeverity() == WorkflowDiagnosticSeverity.ERROR);
    }

    public long getErrorCount() {
        return diagnostics.stream()
                .filter(diagnostic -> diagnostic.getSeverity() == WorkflowDiagnosticSeverity.ERROR)
                .count();
    }

    public long getWarningCount() {
        return diagnostics.stream()
                .filter(diagnostic -> diagnostic.getSeverity() == WorkflowDiagnosticSeverity.WARNING)
                .count();
    }
}
