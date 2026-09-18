package tech.kayys.wayang.harness.kernel;

import java.util.List;
import java.util.Map;

/**
 * Coordinates module lifecycle transitions (initialization, startup, shutdown, destruction).
 */
public interface LifecycleCoordinator {

    void initializeAll(List<WayangModule> modules, ServiceRegistry services, Map<String, Object> configuration);

    void startAll(List<WayangModule> startupOrder);

    void stopAll(List<WayangModule> startupOrder);

    void destroyAll(List<WayangModule> startupOrder);
}
