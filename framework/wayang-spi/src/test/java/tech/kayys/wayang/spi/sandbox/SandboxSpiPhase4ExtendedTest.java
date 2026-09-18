package tech.kayys.wayang.spi.sandbox;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.extension.Version;
import tech.kayys.wayang.spi.sandbox.container.*;
import tech.kayys.wayang.spi.sandbox.vm.*;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SandboxSpiPhase4ExtendedTest {

    @Test
    void testProcessExecutionRequestAndResult() {
        ProcessExecutionRequest req = ProcessExecutionRequest.of(List.of("echo", "test"), Duration.ofSeconds(5));
        assertEquals(2, req.command().size());
        assertEquals(Duration.ofSeconds(5), req.timeout());
        assertFalse(req.inheritEnvironment());

        ProcessExecutionResult res = new ProcessExecutionResult(0, "ok", "", Duration.ofMillis(100), false, false);
        assertTrue(res.successful());
        assertEquals(0, res.exitCodeOptional().orElse(-1));

        ProcessExecutionResult resFail = new ProcessExecutionResult(1, "", "err", Duration.ofMillis(100), false, false, true);
        assertFalse(resFail.successful());
    }

    @Test
    void testFilesystemModels() {
        FilesystemRoot root = new FilesystemRoot("root1", Path.of("/tmp"), FilesystemAccess.READ_WRITE, false);
        assertTrue(root.readable());
        assertTrue(root.writable());
        assertEquals("root1", root.id());

        FilesystemSandboxPolicy policy = FilesystemSandboxPolicy.strict(List.of(root));
        assertTrue(policy.denyAbsolutePaths());
        assertTrue(policy.denyParentTraversal());
        assertTrue(policy.denySymlinkEscape());

        ResolvedSandboxPath path = new ResolvedSandboxPath("root1", Path.of("/tmp/test"), FilesystemAccess.READ);
        assertEquals("root1", path.rootId());
        assertEquals(FilesystemAccess.READ, path.access());

        SandboxFilesystem empty = SandboxFilesystem.empty();
        assertFalse(empty.exists("root1", Path.of("foo")));
        assertThrows(UnsupportedOperationException.class, () -> empty.resolve("root1", Path.of("foo")));
    }

    @Test
    void testNetworkModels() {
        NetworkRule rule = new NetworkRule(NetworkProtocol.TCP, "example.com", 443);
        assertEquals("example.com", rule.host());
        assertEquals(443, rule.port());

        NetworkSandboxPolicy policy = NetworkSandboxPolicy.restricted(List.of(rule));
        assertEquals(NetworkMode.RESTRICTED, policy.mode());
        assertEquals(1, policy.allow().size());

        NetworkRequirement req = new NetworkRequirement(NetworkMode.RESTRICTED, true, false);
        assertTrue(req.requiresIsolation());

        NetworkDecision allow = NetworkDecision.allowed("p1");
        assertTrue(allow instanceof NetworkDecision.Allowed);
        NetworkDecision deny = NetworkDecision.denied("restricted");
        assertTrue(deny instanceof NetworkDecision.Denied);

        SandboxNetwork disabled = SandboxNetwork.disabled();
        assertEquals(NetworkMode.DISABLED, disabled.mode());
        assertFalse(disabled.allows(rule));
        assertThrows(IllegalStateException.class, () -> disabled.validate(rule));

        SandboxNetwork restricted = SandboxNetwork.restricted(List.of(rule));
        assertEquals(NetworkMode.RESTRICTED, restricted.mode());
        assertTrue(restricted.allows(rule));
        assertDoesNotThrow(() -> restricted.validate(rule));
        assertFalse(restricted.allows(new NetworkRule(NetworkProtocol.TCP, "other.com", 80)));
    }

    @Test
    void testLimitsAndCapabilities() {
        SandboxLimits limits = new SandboxLimits(1000L, 2048L, -1L, 10L, Duration.ofSeconds(30), 5000L, 100L);
        assertTrue(limits.hasCpuLimit());
        assertTrue(limits.hasMemoryLimit());
        assertFalse(limits.hasDiskLimit());
        assertTrue(limits.hasProcessLimit());
        assertTrue(limits.hasExecutionTimeout());
        assertTrue(limits.hasOutputLimit());
        assertTrue(limits.hasFileCountLimit());

        ResourceLimitCapability cap = new ResourceLimitCapability(ResourceLimitFeature.CPU_LIMIT, ResourceEnforcement.HARD);
        assertEquals(ResourceLimitFeature.CPU_LIMIT, cap.feature());
        assertEquals(ResourceEnforcement.HARD, cap.enforcement());

        SandboxProviderDescriptor desc = new SandboxProviderDescriptor(
                "p1", "Provider 1", "Desc", Version.parse("1.0.0"),
                Set.of(SandboxType.PROCESS, SandboxType.CONTAINER),
                Set.of(IsolationFeature.PROCESS_ISOLATION),
                Set.of(NetworkIsolationFeature.RESTRICTED_NETWORK),
                Set.of(ResourceLimitFeature.CPU_LIMIT, ResourceLimitFeature.EXECUTION_TIMEOUT),
                Map.of()
        );
        assertTrue(desc.supports(SandboxType.CONTAINER));
        assertTrue(desc.supports(IsolationFeature.PROCESS_ISOLATION));
        assertTrue(desc.supports(NetworkIsolationFeature.RESTRICTED_NETWORK));
        assertTrue(desc.supports(ResourceLimitFeature.CPU_LIMIT));
        assertTrue(desc.supportsAllResourceLimits(Set.of(ResourceLimitFeature.CPU_LIMIT, ResourceLimitFeature.EXECUTION_TIMEOUT)));
    }

    @Test
    void testContainerAndVmRequests() {
        SandboxRequest baseReq = new SandboxRequest(
                "sb-123",
                SandboxType.CONTAINER,
                Set.of(),
                SandboxLimits.unlimited(),
                SandboxFilesystem.empty(),
                SandboxNetwork.disabled(),
                Map.of(),
                Map.of("container.image", "alpine:latest", "vm.image", "debian.qcow2")
        );

        ContainerSandboxRequest containerReq = ContainerSandboxRequest.from(baseReq);
        assertEquals("sb-123", containerReq.sandboxId());
        assertEquals("alpine:latest", containerReq.image());

        VMSandboxRequest vmReq = VMSandboxRequest.from(baseReq);
        assertEquals("sb-123", vmReq.sandboxId());
        assertEquals("debian.qcow2", vmReq.image());

        ContainerSecurityProfile security = ContainerSecurityProfile.hardened();
        assertFalse(security.privileged());
        assertTrue(security.readOnlyRoot());
        assertTrue(security.noNewPrivileges());

        ContainerUser user = ContainerUser.nonRoot(1000, 1000);
        assertFalse(user.isRoot());
        assertEquals(1000, user.uid());
    }

    @Test
    void testPolicyAndStateMachine() {
        SandboxPolicyDecision allow = SandboxPolicyDecision.allow(ExecutionIsolationProfile.none());
        assertTrue(allow instanceof SandboxPolicyDecision.Allow);

        SandboxPolicyDecision deny = SandboxPolicyDecision.deny("Not allowed");
        assertTrue(deny instanceof SandboxPolicyDecision.Deny);

        SandboxPolicyContext pCtx = new SandboxPolicyContext(
                "exec-1", "tenant-1", "user-1", "agent-1", "corr-1",
                ExecutionIsolationProfile.none(), java.util.Optional.empty(),
                java.util.Optional.empty(), java.time.Instant.now(), Map.of()
        );
        assertEquals("exec-1", pCtx.executionId());
        assertEquals("tenant-1", pCtx.tenantId());

        assertEquals(NetworkMode.RESTRICTED, NetworkPolicyIntersection.mode(NetworkMode.FULL, NetworkMode.RESTRICTED));
        assertEquals(NetworkMode.DISABLED, NetworkPolicyIntersection.mode(NetworkMode.RESTRICTED, NetworkMode.DISABLED));

        assertTrue(SandboxStateMachine.canTransition(SandboxState.CREATED, SandboxState.STARTING));
        assertTrue(SandboxStateMachine.canTransition(SandboxState.RUNNING, SandboxState.STOPPING));
        assertTrue(SandboxStateMachine.canTransition(SandboxState.STOPPED, SandboxState.DESTROYED));
        assertFalse(SandboxStateMachine.canTransition(SandboxState.DESTROYED, SandboxState.RUNNING));
    }

    @Test
    void testSandboxContextAndArtifactTransfer() {
        SandboxContextRequest ctxReq = new SandboxContextRequest(
                "exec-1", "tenant-1", "user-1", "agent-1", "sess-1", "corr-1",
                java.time.Instant.now(), Map.of()
        );
        assertEquals("exec-1", ctxReq.executionId());
        assertEquals("tenant-1", ctxReq.tenant().orElse(""));
        assertEquals("user-1", ctxReq.user().orElse(""));

        tech.kayys.wayang.spi.sandbox.artifact.ArtifactDescriptor artDesc =
                new tech.kayys.wayang.spi.sandbox.artifact.ArtifactDescriptor(
                        "art-1", "exec-1", "tenant-1", "sb-1", "file.txt", "text/plain", 100L, "sha256",
                        java.time.Instant.now(), Map.of()
                );
        assertEquals("art-1", artDesc.artifactId());
        assertEquals(100L, artDesc.size());

        tech.kayys.wayang.spi.sandbox.artifact.ArtifactTransferLimits limits =
                tech.kayys.wayang.spi.sandbox.artifact.ArtifactTransferLimits.unlimited();
        assertEquals(-1, limits.maxArtifactBytes());
    }

    @Test
    void testObservabilityAndOperator() {
        tech.kayys.wayang.spi.sandbox.observability.SandboxObservation obs =
                new tech.kayys.wayang.spi.sandbox.observability.SandboxObservation(
                        "obs-1", tech.kayys.wayang.spi.sandbox.observability.SandboxObservationType.CREATED,
                        java.time.Instant.now(), "sb-1", "exec-1", "tenant-1", "agent-1", "corr-1", "process", Map.of()
                );
        assertEquals("obs-1", obs.observationId());
        assertEquals("tenant-1", obs.tenant().orElse(""));

        tech.kayys.wayang.spi.sandbox.observability.SandboxHealth health =
                new tech.kayys.wayang.spi.sandbox.observability.SandboxHealth(
                        "sb-1", tech.kayys.wayang.spi.sandbox.observability.SandboxHealthStatus.HEALTHY,
                        java.time.Instant.now(), "All good", Map.of()
                );
        assertEquals(tech.kayys.wayang.spi.sandbox.observability.SandboxHealthStatus.HEALTHY, health.status());

        tech.kayys.wayang.spi.operator.OperatorPermission perm =
                new tech.kayys.wayang.spi.operator.OperatorPermission("operator.sandboxes.read", "Read sandboxes");
        assertEquals("operator.sandboxes.read", perm.id());

        tech.kayys.wayang.spi.operator.OperatorResult<String> res =
                tech.kayys.wayang.spi.operator.OperatorResult.success("ok");
        assertTrue(res instanceof tech.kayys.wayang.spi.operator.OperatorResult.Success);
    }
}
