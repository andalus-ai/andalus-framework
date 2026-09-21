package tech.kayys.andalus.skill.spi;

import tech.kayys.andalus.extension.Extension;

public interface Skill extends Extension {
    SkillDescriptor descriptor();
    SkillResult execute(SkillContext context);
}
