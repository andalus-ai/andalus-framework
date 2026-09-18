package tech.kayys.wayang.harness.coordination.messaging;

import tech.kayys.wayang.harness.protocol.AgentRef;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Message bus facilitating asynchronous asynchronous communication and result transfer between agents.
 */
public interface AgentMessageBus {

    void send(AgentMessage message);

    Collection<AgentMessage> poll(AgentRef recipient);

    static AgentMessageBus inMemory() {
        return new InMemoryAgentMessageBus();
    }
}

class InMemoryAgentMessageBus implements AgentMessageBus {
    private final Map<String, List<AgentMessage>> inboxes = new ConcurrentHashMap<>();

    @Override
    public void send(AgentMessage message) {
        if (message != null) {
            inboxes.computeIfAbsent(message.recipient().agentId(), k -> new CopyOnWriteArrayList<>()).add(message);
        }
    }

    @Override
    public Collection<AgentMessage> poll(AgentRef recipient) {
        if (recipient == null) return List.of();
        List<AgentMessage> inbox = inboxes.get(recipient.agentId());
        if (inbox == null || inbox.isEmpty()) return List.of();
        List<AgentMessage> copy = List.copyOf(inbox);
        inbox.clear();
        return copy;
    }
}
