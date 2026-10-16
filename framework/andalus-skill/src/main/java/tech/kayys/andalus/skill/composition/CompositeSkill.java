package tech.kayys.andalus.skill.composition;

import tech.kayys.andalus.extension.Metadata;
import tech.kayys.andalus.resource.BaseResource;
import tech.kayys.andalus.skill.spi.Skill;
import tech.kayys.andalus.skill.spi.SkillContext;
import tech.kayys.andalus.skill.spi.SkillDescriptor;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * A {@link Skill} implementation that delegates execution to a
 * {@link CompositeSkillRuntime} evaluating a {@link SkillComposition}.
 */
public final class CompositeSkill extends BaseResource implements Skill {

    private final SkillDescriptor descriptor;
    private final SkillComposition composition;
    private final CompositeSkillRuntime runtime;

    public CompositeSkill(
            SkillDescriptor descriptor,
            SkillComposition composition,
            CompositeSkillRuntime runtime) {

        super(
                Objects.requireNonNull(descriptor, "descriptor must not be null").id(),
                descriptor.metadata() != null ? descriptor.metadata() : Metadata.empty()
        );

        this.descriptor  = descriptor;
        this.composition = Objects.requireNonNull(composition, "composition must not be null");
        this.runtime     = Objects.requireNonNull(runtime, "runtime must not be null");
    }

    @Override
    public SkillDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public SkillResult execute(SkillContext context) {
        try {
            return runtime.execute(composition, context).toCompletableFuture().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return SkillResult.failure(descriptor.id().asString(), e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            return SkillResult.failure(descriptor.id().asString(), cause);
        }
    }

    public SkillComposition composition() {
        return composition;
    }
}
