package tech.kayys.andalus.communication.core.protocol;

import tech.kayys.andalus.communication.api.AgentRequest;
import tech.kayys.andalus.communication.exception.NoCompatibleProtocolException;
import tech.kayys.andalus.communication.protocol.ProtocolContext;

import java.util.List;

public interface ProtocolRouter {

    List<ProtocolCandidate> candidates(
            AgentRequest request,
            ProtocolContext context
    );

    default ProtocolCandidate route(
            AgentRequest request,
            ProtocolContext context
    ) {
        List<ProtocolCandidate> list = candidates(request, context);
        if (list.isEmpty()) {
            throw new NoCompatibleProtocolException(request);
        }
        return list.getFirst();
    }
}
