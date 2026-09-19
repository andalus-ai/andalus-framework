package tech.kayys.wayang.spi.operator.tool;

import tech.kayys.wayang.spi.operator.OperatorContext;
import tech.kayys.wayang.spi.operator.OperatorResult;

import java.util.List;

public interface ToolCapabilityOperatorService {

    OperatorResult<List<CapabilitySummary>> listCapabilities(
            OperatorContext context);

    OperatorResult<CapabilitySummary> inspectCapability(
            OperatorContext context,
            String capabilityId);

    OperatorResult<List<CapabilitySummary>> findCapabilitiesByType(
            OperatorContext context,
            String type);

    OperatorResult<List<CapabilitySummary>> findCapabilitiesByTag(
            OperatorContext context,
            String tag);

    OperatorResult<List<ToolSummary>> listTools(
            OperatorContext context);

    OperatorResult<ToolSummary> inspectTool(
            OperatorContext context,
            String toolName);

    OperatorResult<List<ToolSummary>> findToolsByCapability(
            OperatorContext context,
            String capabilityId);
}
