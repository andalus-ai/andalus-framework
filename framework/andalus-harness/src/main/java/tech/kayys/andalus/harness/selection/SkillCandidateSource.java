package tech.kayys.andalus.harness.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Discovers candidate skills available for selection.
 */
public class SkillCandidateSource implements CandidateSource {

    private final ConcurrentHashMap<String, CandidateDescriptor> registeredSkills = new ConcurrentHashMap<>();

    public SkillCandidateSource() {}

    public SkillCandidateSource(Collection<CandidateDescriptor> initialSkills) {
        if (initialSkills != null) {
            for (CandidateDescriptor s : initialSkills) {
                registerSkill(s);
            }
        }
    }

    public void registerSkill(CandidateDescriptor skill) {
        if (skill != null) {
            registeredSkills.put(skill.id(), skill);
        }
    }

    @Override
    public SelectionTarget supportedTarget() {
        return SelectionTarget.SKILL;
    }

    @Override
    public CompletionStage<List<CandidateDescriptor>> discover(SelectionRequest request) {
        List<CandidateDescriptor> candidates = new ArrayList<>(registeredSkills.values());
        return CompletableFuture.completedFuture(candidates);
    }
}
