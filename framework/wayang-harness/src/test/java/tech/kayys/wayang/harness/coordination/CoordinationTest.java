package tech.kayys.wayang.harness.coordination;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.coordination.messaging.AgentMessage;
import tech.kayys.wayang.harness.coordination.messaging.AgentMessageBus;
import tech.kayys.wayang.harness.coordination.task.CoordinationTaskId;
import tech.kayys.wayang.harness.coordination.task.CoordinationTaskState;
import tech.kayys.wayang.harness.coordination.task.DelegatedTask;
import tech.kayys.wayang.harness.protocol.AgentRef;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CoordinationTest {

    @Test
    void testDelegatedTaskAssignment() {
        DelegatedTask task = DelegatedTask.of("Refactor database schema");
        assertNotNull(task.id());
        assertEquals(CoordinationTaskState.PENDING, task.state());
        assertTrue(task.assignedAgent().isEmpty());

        AgentRef agent = AgentRef.of("db-expert");
        DelegatedTask assigned = task.assignTo(agent);

        assertEquals(CoordinationTaskState.ASSIGNED, assigned.state());
        assertTrue(assigned.assignedAgent().isPresent());
        assertEquals(agent.agentId(), assigned.assignedAgent().get().agentId());
    }

    @Test
    void testAgentMessageBusDispatch() {
        AgentMessageBus bus = AgentMessageBus.inMemory();
        AgentRef sender = AgentRef.of("coordinator");
        AgentRef recipient = AgentRef.of("worker-1");

        AgentMessage msg = AgentMessage.of(sender, recipient, "TASK_OFFER", "Please run unit tests");
        bus.send(msg);

        Collection<AgentMessage> inbox = bus.poll(recipient);
        assertEquals(1, inbox.size());
        assertEquals("TASK_OFFER", inbox.iterator().next().messageType());

        // Second poll should be empty
        assertTrue(bus.poll(recipient).isEmpty());
    }
}
