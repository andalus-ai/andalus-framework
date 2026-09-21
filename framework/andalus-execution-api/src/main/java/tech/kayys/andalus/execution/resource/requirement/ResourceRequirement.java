package tech.kayys.andalus.execution.resource.requirement;

import tech.kayys.andalus.execution.resource.model.ResourceId;
import tech.kayys.andalus.execution.resource.model.ResourceType;

/**
 * Base contract for a logical resource requirement.
 */
public interface ResourceRequirement {

    ResourceId resourceId();

    ResourceType type();
}
