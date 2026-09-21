package tech.kayys.andalus.spi.operator.diagnostics;

import tech.kayys.andalus.spi.diagnostics.DiagnosticComponent;
import tech.kayys.andalus.spi.diagnostics.DiagnosticComponentType;
import tech.kayys.andalus.spi.diagnostics.DiagnosticReport;
import tech.kayys.andalus.spi.diagnostics.DiagnosticResult;
import tech.kayys.andalus.spi.operator.OperatorContext;
import tech.kayys.andalus.spi.operator.OperatorResult;

import java.util.List;

public interface DiagnosticsOperatorService {

    OperatorResult<DiagnosticReport> diagnoseAll(
            OperatorContext context);

    OperatorResult<DiagnosticResult> diagnose(
            OperatorContext context,
            DiagnosticComponent component);

    OperatorResult<List<DiagnosticResult>> diagnoseType(
            OperatorContext context,
            DiagnosticComponentType type);
}
