package tech.kayys.wayang.spi.diagnostics;

import java.util.List;
import java.util.Optional;

public interface DiagnosticProviderRegistry {

    Optional<DiagnosticProvider> find(String id);

    List<DiagnosticProvider> findAll();

    List<DiagnosticProvider> findByType(
            DiagnosticComponentType type);
}
