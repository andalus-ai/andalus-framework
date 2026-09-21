package tech.kayys.andalus.spi.diagnostics;

public interface DiagnosticProvider {

    String id();

    DiagnosticComponentType componentType();

    DiagnosticResult diagnose(
            DiagnosticContext context)
            throws Exception;
}
