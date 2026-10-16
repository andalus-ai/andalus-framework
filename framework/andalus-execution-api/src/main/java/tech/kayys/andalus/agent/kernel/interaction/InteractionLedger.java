
package tech.kayys.andalus.agent.kernel.interaction;

import java.util.List;

public interface InteractionLedger {

    InteractionRecord append(
            InteractionRecord record
    );

    List<InteractionRecord> history(
            String runId,
            long afterSequence
    );

    List<InteractionRecord> historyForRequest(
            String requestId
    );

    long latestSequence(
            String runId
    );
}
