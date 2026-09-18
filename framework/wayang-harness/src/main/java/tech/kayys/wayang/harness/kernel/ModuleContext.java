package tech.kayys.wayang.harness.kernel;

import java.util.Map;

/**
 * Context provided to a module during initialization, offering access to infrastructure
 * and services without exposing the entire HarnessKernel god object.
 */
public interface ModuleContext {

    ModuleId moduleId();

    ServiceRegistry services();

    Map<String, Object> configuration();
}
