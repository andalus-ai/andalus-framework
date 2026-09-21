package tech.kayys.andalus.spi.operator.sandbox;

import tech.kayys.andalus.spi.operator.OperatorContext;
import tech.kayys.andalus.spi.operator.OperatorResult;

import java.util.List;

public interface SandboxOperatorService {

    OperatorResult<List<SandboxSummary>> list(
            OperatorContext context);

    OperatorResult<SandboxSummary> inspect(
            OperatorContext context,
            String sandboxId);

    OperatorResult<SandboxHealthSummary> health(
            OperatorContext context,
            String sandboxId);

    OperatorResult<SandboxMetricsSummary> metrics(
            OperatorContext context,
            String sandboxId);

    OperatorResult<SandboxDiagnosticsSummary> diagnostics(
            OperatorContext context,
            String sandboxId);

    OperatorResult<Void> stop(
            OperatorContext context,
            String sandboxId);

    OperatorResult<Void> destroy(
            OperatorContext context,
            String sandboxId);
}
