package tech.kayys.andalus.harness.reflection;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Result of evaluating an execution turn or outcome through reflection.
 */
public record ReflectionResult(
        String reflectionId,
        ReflectionStatus status,
        List<ReflectionFinding> findings,
        List<ReflectionProposal> proposals,
        List<String> evidenceReferences,
        Instant completedAt,
        Map<String, Object> metadata
) {
    public ReflectionResult {
        reflectionId = reflectionId == null || reflectionId.isBlank() ? UUID.randomUUID().toString() : reflectionId;
        Objects.requireNonNull(status, "status is required");
        findings = findings == null ? List.of() : List.copyOf(findings);
        proposals = proposals == null ? List.of() : List.copyOf(proposals);
        evidenceReferences = evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
        completedAt = completedAt == null ? Instant.now() : completedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ReflectionResult completed(
            List<ReflectionFinding> findings,
            List<ReflectionProposal> proposals,
            List<String> evidenceReferences
    ) {
        return new ReflectionResult(
                UUID.randomUUID().toString(),
                ReflectionStatus.COMPLETED,
                findings,
                proposals,
                evidenceReferences,
                Instant.now(),
                Map.of()
        );
    }

    public static ReflectionResult insufficientEvidence(String reason) {
        return new ReflectionResult(
                UUID.randomUUID().toString(),
                ReflectionStatus.INSUFFICIENT_EVIDENCE,
                List.of(),
                List.of(),
                List.of(),
                Instant.now(),
                Map.of("reason", reason != null ? reason : "")
        );
    }

    public static ReflectionResult failed(String errorMessage) {
        return new ReflectionResult(
                UUID.randomUUID().toString(),
                ReflectionStatus.FAILED,
                List.of(),
                List.of(),
                List.of(),
                Instant.now(),
                Map.of("error", errorMessage != null ? errorMessage : "")
        );
    }

    public boolean isSuccess() {
        return status.isSuccess();
    }
}
