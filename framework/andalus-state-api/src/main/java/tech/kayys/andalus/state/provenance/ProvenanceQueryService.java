package tech.kayys.andalus.state.provenance;

import tech.kayys.andalus.state.artifact.ArtifactId;
import tech.kayys.andalus.state.artifact.ArtifactReference;
import tech.kayys.andalus.state.lineage.LineageGraph;

import java.util.List;

public interface ProvenanceQueryService {
    LineageGraph traceArtifact(ArtifactId artifact);
    LineageGraph traceExecution(String executionId);
    List<ArtifactReference> inputsOf(OperationId operation);
    List<ArtifactReference> outputsOf(OperationId operation);
}
