package tech.kayys.wayang.spi.diagnostics;

import java.util.List;

public interface DiagnosticService {

    DiagnosticResult diagnose(
            DiagnosticContext context,
            DiagnosticComponent component)
            throws Exception;

    List<DiagnosticResult> diagnoseAll(
            DiagnosticContext context)
            throws Exception;

    List<DiagnosticResult> diagnoseType(
            DiagnosticContext context,
            DiagnosticComponentType type)
            throws Exception;
}
