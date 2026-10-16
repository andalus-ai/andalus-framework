package tech.kayys.andalus.agent.spi.skills;

public record SkillHealth(String skillId, boolean healthy, String message) {
    public static SkillHealth healthy(String skillId) {
        return new SkillHealth(skillId, true, "OK");
    }

    public static SkillHealth unhealthy(String skillId, String message) {
        return new SkillHealth(skillId, false, message);
    }
}
