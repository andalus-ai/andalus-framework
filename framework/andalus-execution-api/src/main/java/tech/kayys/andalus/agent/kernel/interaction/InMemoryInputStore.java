
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryInputStore
        implements InputStore {

    private final Map<String, InputRequest> requests =
            new ConcurrentHashMap<>();

    private final Map<String, InputResponse> responses =
            new ConcurrentHashMap<>();

    @Override
    public InputRequest save(
            InputRequest request
    ) {
        requests.put(
                request.id(),
                request
        );

        return request;
    }

    @Override
    public Optional<InputRequest> find(
            String requestId
    ) {
        return Optional.ofNullable(
                requests.get(requestId)
        );
    }

    @Override
    public InputResponse submit(
            InputResponse response
    ) {
        if (!requests.containsKey(response.requestId())) {
            throw new IllegalArgumentException(
                    "Unknown input request: "
                            + response.requestId()
            );
        }

        responses.put(
                response.requestId(),
                response
        );

        return response;
    }

    @Override
    public Optional<InputResponse> response(
            String requestId
    ) {
        return Optional.ofNullable(
                responses.get(requestId)
        );
    }
}
