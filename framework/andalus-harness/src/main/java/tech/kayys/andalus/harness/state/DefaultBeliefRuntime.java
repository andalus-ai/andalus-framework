package tech.kayys.andalus.harness.state;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default in-memory implementation of {@link BeliefRuntime}.
 *
 * <p>Enforces optimistic revision control on belief updates, validates state transitions,
 * and produces immutable {@link BeliefSnapshot} and {@link AgentStateSnapshot} views.</p>
 */
public class DefaultBeliefRuntime implements BeliefRuntime {

    private final ConcurrentHashMap<String, ExecutionStateHolder> stateByExecution = new ConcurrentHashMap<>();

    private static class ExecutionStateHolder {
        long revision = 0L;
        final Map<String, Belief> beliefs = new HashMap<>();
        final List<String> unresolvedQuestions = new ArrayList<>();
        final List<String> observationReferences = new ArrayList<>();
        Optional<String> activeGoalId = Optional.empty();
        Optional<String> activeTaskId = Optional.empty();
        Optional<String> checkpointReference = Optional.empty();
    }

    private ExecutionStateHolder getOrCreate(String executionId) {
        return stateByExecution.computeIfAbsent(executionId, k -> new ExecutionStateHolder());
    }

    public void setActiveGoalAndTask(String executionId, String goalId, String taskId) {
        ExecutionStateHolder holder = getOrCreate(executionId);
        synchronized (holder) {
            holder.activeGoalId = Optional.ofNullable(goalId);
            holder.activeTaskId = Optional.ofNullable(taskId);
            holder.revision++;
        }
    }

    public void recordObservationReference(String executionId, String observationId) {
        if (observationId != null) {
            ExecutionStateHolder holder = getOrCreate(executionId);
            synchronized (holder) {
                holder.observationReferences.add(observationId);
                holder.revision++;
            }
        }
    }

    public void recordUnresolvedQuestion(String executionId, String question) {
        if (question != null) {
            ExecutionStateHolder holder = getOrCreate(executionId);
            synchronized (holder) {
                holder.unresolvedQuestions.add(question);
                holder.revision++;
            }
        }
    }

    @Override
    public CompletionStage<BeliefUpdateResult> update(BeliefUpdateRequest request) {
        if (request == null) {
            return CompletableFuture.completedFuture(BeliefUpdateResult.failed("Request cannot be null"));
        }

        ExecutionStateHolder holder = getOrCreate(request.executionId());

        synchronized (holder) {
            if (request.existingBeliefId().isEmpty()) {
                // Creation flow
                String id = UUID.randomUUID().toString();
                Belief belief = new Belief(
                        id,
                        request.subject(),
                        request.proposition(),
                        BeliefStatus.ACTIVE,
                        request.evidence(),
                        Optional.empty(),
                        1L,
                        Instant.now(),
                        Instant.now(),
                        request.metadata()
                );
                holder.beliefs.put(id, belief);
                holder.revision++;
                return CompletableFuture.completedFuture(BeliefUpdateResult.created(belief, holder.revision));
            }

            // Update/transition flow
            String beliefId = request.existingBeliefId().get();
            Belief existing = holder.beliefs.get(beliefId);
            if (existing == null) {
                return CompletableFuture.completedFuture(BeliefUpdateResult.notFound(beliefId));
            }

            if (request.expectedRevision() > 0 && request.expectedRevision() != existing.revision()) {
                String conflictMsg = "Optimistic locking conflict: expected revision "
                        + request.expectedRevision() + " but was " + existing.revision();
                return CompletableFuture.completedFuture(BeliefUpdateResult.conflict(existing.revision(), conflictMsg));
            }

            BeliefStatus newStatus = existing.status();
            if (request.reason() == BeliefUpdateReason.CONTRADICTING_EVIDENCE) {
                newStatus = BeliefStatus.DISPUTED;
            } else if (request.reason() == BeliefUpdateReason.EXPLICIT_RETRACTION) {
                newStatus = BeliefStatus.RETRACTED;
            } else if (request.reason() == BeliefUpdateReason.VERIFICATION || request.reason() == BeliefUpdateReason.NEW_EVIDENCE) {
                newStatus = BeliefStatus.ACTIVE;
            }

            List<BeliefEvidenceReference> combinedEvidence = new ArrayList<>(existing.evidence());
            combinedEvidence.addAll(request.evidence());

            String newProposition = request.proposition().isBlank() || "RETRACTED".equals(request.proposition())
                    ? existing.proposition()
                    : request.proposition();

            Belief updated = new Belief(
                    existing.id(),
                    request.subject().isBlank() ? existing.subject() : request.subject(),
                    newProposition,
                    newStatus,
                    combinedEvidence,
                    existing.supersedesBeliefId(),
                    existing.revision() + 1,
                    existing.createdAt(),
                    Instant.now(),
                    request.metadata()
            );

            holder.beliefs.put(beliefId, updated);
            holder.revision++;
            return CompletableFuture.completedFuture(BeliefUpdateResult.updated(updated, holder.revision));
        }
    }

    @Override
    public CompletionStage<BeliefSnapshot> snapshot(String executionId) {
        ExecutionStateHolder holder = getOrCreate(executionId);
        synchronized (holder) {
            List<Belief> copy = List.copyOf(holder.beliefs.values());
            return CompletableFuture.completedFuture(
                    new BeliefSnapshot(executionId, holder.revision, copy, Instant.now(), Map.of())
            );
        }
    }

    @Override
    public CompletionStage<AgentStateSnapshot> stateSnapshot(String executionId) {
        ExecutionStateHolder holder = getOrCreate(executionId);
        synchronized (holder) {
            List<BeliefReference> references = holder.beliefs.values().stream()
                    .map(Belief::toReference)
                    .toList();

            AgentStateSnapshot snapshot = new AgentStateSnapshot(
                    executionId,
                    holder.revision,
                    holder.activeGoalId,
                    holder.activeTaskId,
                    references,
                    List.copyOf(holder.unresolvedQuestions),
                    List.copyOf(holder.observationReferences),
                    holder.checkpointReference,
                    Instant.now(),
                    Map.of()
            );
            return CompletableFuture.completedFuture(snapshot);
        }
    }
}
