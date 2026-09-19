package tech.kayys.wayang.spi.operator.diagnostics;

import tech.kayys.wayang.spi.diagnostics.DiagnosticComponent;
import tech.kayys.wayang.spi.diagnostics.DiagnosticComponentType;
import tech.kayys.wayang.spi.diagnostics.DiagnosticReport;
import tech.kayys.wayang.spi.diagnostics.DiagnosticResult;
import tech.kayys.wayang.spi.operator.OperatorContext;
import tech.kayys.wayang.spi.operator.OperatorResult;

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
