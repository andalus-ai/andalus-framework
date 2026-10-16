package tech.kayys.andalus.agent.spi.skills;

import io.smallrye.mutiny.Uni;
import tech.kayys.andalus.agent.spi.AgentSkill;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface SkillRegistry {
    List<AgentSkill> listAll();
    Optional<AgentSkill> find(String id);
    AgentSkill findOrThrow(String id);
    void register(AgentSkill skill);
    void unregister(String skillId);
    List<AgentSkill> findByCategory(SkillCategory category);
    List<AgentSkill> listAllowed(String tenantId, Set<String> allowedIds);
    boolean isRegistered(String skillId);
    Map<String, SkillHealth> checkHealth();
    int size();
    Optional<SkillDefinition> getSkill(String skillId);
    List<SkillDefinition> listSkills();
    List<SkillDefinition> listByCategory(String category);
    void registerSkill(SkillDefinition skill);
    boolean unregisterSkill(String skillId);

    default List<SkillDefinition> list() {
        return listSkills();
    }

    default List<SkillDefinition> findAll() {
        return listSkills();
    }

    default Uni<SkillDefinition> get(String id) {
        return Uni.createFrom().optional(getSkill(id));
    }

    default Uni<SkillResult> executeSkill(String id, Map<String, Object> context) {
        return Uni.createFrom().failure(new UnsupportedOperationException("Not implemented"));
    }

    default boolean hasSkill(String id) {
        return isRegistered(id);
    }
}
