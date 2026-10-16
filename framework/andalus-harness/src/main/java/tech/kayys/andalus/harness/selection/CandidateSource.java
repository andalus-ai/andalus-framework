package tech.kayys.andalus.harness.selection;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * SPI for discovering candidate tools or skills available for evaluation during selection.
 */
public interface CandidateSource {

    /**
     * The candidate target supported by this source.
     */
    SelectionTarget supportedTarget();

    /**
     * Discovers candidate descriptors matching the given selection request.
     *
     * @param request the selection request
     * @return a completion stage resolving to candidate descriptors
     */
    CompletionStage<List<CandidateDescriptor>> discover(SelectionRequest request);
}
