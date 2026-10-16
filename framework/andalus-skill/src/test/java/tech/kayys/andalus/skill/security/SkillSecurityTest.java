package tech.kayys.andalus.skill.security;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.skill.runtime.SkillExecutionRequest;
import tech.kayys.andalus.skill.runtime.SkillRuntime;
import tech.kayys.andalus.skill.spi.SkillReference;
import tech.kayys.andalus.skill.spi.SkillResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class SkillSecurityTest {

    @Test
    void governedRuntimeAllowsWhenPolicyPermits() throws Exception {
        SkillRuntime mockRuntime = req -> CompletableFuture.completedFuture(
                SkillResult.success(Map.of("data", "secure-data"))
        );

        GovernedSkillRuntime governed = new GovernedSkillRuntime(
                mockRuntime,
                SkillSecurityPolicy.permissive()
        );

        SkillExecutionRequest request = SkillExecutionRequest.of(
                SkillReference.of("authorized-skill"), Map.of()
        );

        SkillResult result = governed.execute(request).toCompletableFuture().get();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getOutputs()).containsEntry("data", "secure-data");
    }

    @Test
    void governedRuntimeRejectsWhenPolicyDenies() throws Exception {
        SkillRuntime mockRuntime = req -> CompletableFuture.completedFuture(
                SkillResult.success(Map.of("data", "should-never-see"))
        );

        SkillSecurityPolicy denyAllPolicy = (req, ctx) ->
                SkillSecurityDecision.deny("Unauthorized role for skill " + req.skill().id());

        GovernedSkillRuntime governed = new GovernedSkillRuntime(
                mockRuntime,
                denyAllPolicy
        );

        SkillExecutionRequest request = SkillExecutionRequest.of(
                SkillReference.of("restricted-skill"), Map.of()
        );

        SkillResult result = governed.execute(request).toCompletableFuture().get();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getObservation()).contains("Security policy denied invocation");
        assertThat(result.getObservation()).contains("Unauthorized role");
    }

    @Test
    void securityContextFactoryMethods() {
        SkillSecurityContext unauth = SkillSecurityContext.unauthenticated();
        assertThat(unauth.authenticated()).isFalse();
        assertThat(unauth.principal()).isEmpty();
        assertThat(unauth.attributes()).isEmpty();

        SkillSecurityContext auth = SkillSecurityContext.authenticated("alice", Map.of("role", "admin"));
        assertThat(auth.authenticated()).isTrue();
        assertThat(auth.principal()).contains("alice");
        assertThat(auth.attributes()).containsEntry("role", "admin");
    }
}
