package tech.kayys.andalus.spi.operator.configuration;

import tech.kayys.andalus.spi.operator.OperatorContext;
import tech.kayys.andalus.spi.operator.OperatorResult;
import tech.kayys.andalus.spi.operator.OperatorService;

import java.util.List;

/**
 * Privileged operator facade for configuring Andalus platform and tenants.
 */
public interface ConfigurationOperatorService extends OperatorService {

    OperatorResult<List<ConfigurationSummary>> list(
            OperatorContext context);

    OperatorResult<ConfigurationSummary> inspect(
            OperatorContext context,
            String configurationId);

    OperatorResult<ConfigurationSummary> active(
            OperatorContext context);

    OperatorResult<ConfigurationSummary> update(
            OperatorContext context,
            String configurationId,
            ConfigurationUpdateRequest request);

    OperatorResult<Void> activate(
            OperatorContext context,
            String configurationId);
}
