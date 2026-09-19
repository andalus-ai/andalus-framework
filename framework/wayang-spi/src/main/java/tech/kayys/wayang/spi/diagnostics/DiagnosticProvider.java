package tech.kayys.wayang.spi.diagnostics;

public interface DiagnosticProvider {

    String id();

    DiagnosticComponentType componentType();

    DiagnosticResult diagnose(
            DiagnosticContext context)
            throws Exception;
}
