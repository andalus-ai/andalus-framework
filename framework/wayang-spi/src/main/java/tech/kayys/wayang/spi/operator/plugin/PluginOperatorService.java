package tech.kayys.wayang.spi.operator.plugin;

import tech.kayys.wayang.spi.operator.OperatorContext;
import tech.kayys.wayang.spi.operator.OperatorResult;

import java.util.List;

public interface PluginOperatorService {

    OperatorResult<List<PluginSummary>> list(
            OperatorContext context);

    OperatorResult<PluginSummary> inspect(
            OperatorContext context,
            String pluginId);

    OperatorResult<Void> enable(
            OperatorContext context,
            String pluginId);

    OperatorResult<Void> disable(
            OperatorContext context,
            String pluginId);

    OperatorResult<Void> unload(
            OperatorContext context,
            String pluginId);
}
