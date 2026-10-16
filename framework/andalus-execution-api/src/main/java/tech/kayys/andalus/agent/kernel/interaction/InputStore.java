
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.Optional;

public interface InputStore {

    InputRequest save(
            InputRequest request
    );

    Optional<InputRequest> find(
            String requestId
    );

    InputResponse submit(
            InputResponse response
    );

    Optional<InputResponse> response(
            String requestId
    );
}
