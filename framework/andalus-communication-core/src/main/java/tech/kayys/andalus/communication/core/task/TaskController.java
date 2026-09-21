package tech.kayys.andalus.communication.core.task;

import tech.kayys.andalus.communication.api.AgentError;
import tech.kayys.andalus.communication.api.AgentEvent;
import tech.kayys.andalus.communication.api.AgentResult;
import tech.kayys.andalus.communication.task.TaskStatus;

import java.util.function.Consumer;

public interface TaskController {

    void updateStatus(TaskStatus status);

    void publishEvent(AgentEvent event);

    void complete(AgentResult result);

    void fail(AgentError error);

    void onCancel(Runnable cancelHandler);
}
