package tech.kayys.andalus.skill.composition;

import tech.kayys.andalus.skill.spi.SkillContext;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.concurrent.CompletionStage;

/**
 * Runtime coordinator for executing a {@link SkillComposition}.
 */
public interface CompositeSkillRuntime {

    CompletionStage<SkillResult> execute(
            SkillComposition composition,
            SkillContext context
    );
}
