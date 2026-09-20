package tech.kayys.wayang.execution.resource.requirement;

import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.ResourceType;

/**
 * Base contract for a logical resource requirement.
 */
public interface ResourceRequirement {

    ResourceId resourceId();

    ResourceType type();
}
