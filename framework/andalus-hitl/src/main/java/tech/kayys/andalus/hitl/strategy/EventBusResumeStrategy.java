package tech.kayys.andalus.hitl.strategy;

import tech.kayys.andalus.agent.Agent;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * An event-driven strategy: Automatically resumes the agent when the task is approved.
 * Publishes a JSON event to the Vert.x EventBus.
 */
@ApplicationScoped
public class EventBusResumeStrategy implements ResumeStrategy {

    @Inject
    EventBus eventBus;

    @Override
    public void onTaskApproved(Agent agent, String taskId) {
        JsonObject eventJson = new JsonObject().put("taskId", taskId);
        eventBus.publish("andalus.hitl.task.approved", eventJson);
        System.out.println("Published task approved event for task: " + taskId);
    }
}
