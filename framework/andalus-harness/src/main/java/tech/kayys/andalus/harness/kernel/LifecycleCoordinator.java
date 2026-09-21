package tech.kayys.andalus.harness.kernel;

import java.util.List;
import java.util.Map;

/**
 * Coordinates module lifecycle transitions (initialization, startup, shutdown, destruction).
 */
public interface LifecycleCoordinator {

    void initializeAll(List<AndalusModule> modules, ServiceRegistry services, Map<String, Object> configuration);

    void startAll(List<AndalusModule> startupOrder);

    void stopAll(List<AndalusModule> startupOrder);

    void destroyAll(List<AndalusModule> startupOrder);
}
