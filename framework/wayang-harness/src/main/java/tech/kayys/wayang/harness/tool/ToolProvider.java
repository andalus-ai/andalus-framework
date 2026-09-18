package tech.kayys.wayang.harness.tool;

import java.util.Collection;
import java.util.Optional;

public interface ToolProvider {

    Optional<ToolDescriptor> describe(ToolId id);

    Collection<ToolDescriptor> tools();

    ToolExecutor executor(ToolId id);
}
