package tech.kayys.andalus.state.core;

import tech.kayys.andalus.state.artifact.ArtifactId;
import tech.kayys.andalus.state.artifact.ArtifactReference;
import tech.kayys.andalus.state.lineage.LineageGraph;
import tech.kayys.andalus.state.lineage.LineageResolver;
import tech.kayys.andalus.state.provenance.OperationId;
import tech.kayys.andalus.state.provenance.ProvenanceQueryService;
import tech.kayys.andalus.state.provenance.ProvenanceStore;

import java.util.List;
import java.util.Objects;

public class DefaultProvenanceQueryService implements ProvenanceQueryService {

    private final ProvenanceStore provenanceStore;
    private final LineageResolver lineageResolver;

    public DefaultProvenanceQueryService(ProvenanceStore provenanceStore, LineageResolver lineageResolver) {
        this.provenanceStore = Objects.requireNonNull(provenanceStore, "provenanceStore cannot be null");
        this.lineageResolver = Objects.requireNonNull(lineageResolver, "lineageResolver cannot be null");
    }

    @Override
    public LineageGraph traceArtifact(ArtifactId artifact) {
        return lineageResolver.resolve(artifact);
    }

    @Override
    public LineageGraph traceExecution(String executionId) {
        return lineageResolver.resolveExecution(executionId);
    }

    @Override
    public List<ArtifactReference> inputsOf(OperationId operation) {
        return List.of();
    }

    @Override
    public List<ArtifactReference> outputsOf(OperationId operation) {
        return List.of();
    }
}
