package tech.kayys.andalus.harness.learning;

import java.util.concurrent.CompletionStage;

/**
 * Primary SPI for ingesting, validating, and querying learning signals.
 */
public interface LearningSignalRuntime {

    /**
     * Ingests and validates a learning signal, checking for duplicates and reference integrity.
     *
     * @param signal the learning signal record
     * @return completion stage resolving to ingestion result
     */
    CompletionStage<LearningSignalIngestionResult> ingest(LearningSignal signal);

    /**
     * Queries stored learning signals matching the filter criteria.
     *
     * @param query the query criteria
     * @return completion stage resolving to query result
     */
    CompletionStage<LearningSignalQueryResult> query(LearningSignalQuery query);
}
