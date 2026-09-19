package tech.kayys.wayang.state.provenance;

import tech.kayys.wayang.state.artifact.ArtifactId;
import tech.kayys.wayang.state.artifact.ArtifactReference;
import tech.kayys.wayang.state.lineage.LineageGraph;

import java.util.List;

public interface ProvenanceQueryService {
    LineageGraph traceArtifact(ArtifactId artifact);
    LineageGraph traceExecution(String executionId);
    List<ArtifactReference> inputsOf(OperationId operation);
    List<ArtifactReference> outputsOf(OperationId operation);
}
