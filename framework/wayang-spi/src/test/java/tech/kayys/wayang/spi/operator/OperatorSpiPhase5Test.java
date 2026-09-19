package tech.kayys.wayang.spi.operator;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.spi.capability.CapabilityType;
import tech.kayys.wayang.spi.diagnostics.*;
import tech.kayys.wayang.spi.execution.*;
import tech.kayys.wayang.spi.operator.diagnostics.DiagnosticsOperatorPermissions;
import tech.kayys.wayang.spi.operator.execution.ExecutionOperatorPermissions;
import tech.kayys.wayang.spi.operator.plugin.PluginOperatorPermissions;
import tech.kayys.wayang.spi.operator.plugin.PluginSummary;
import tech.kayys.wayang.spi.operator.sandbox.*;
import tech.kayys.wayang.spi.operator.session.SessionOperatorPermissions;
import tech.kayys.wayang.spi.operator.tool.CapabilitySummary;
import tech.kayys.wayang.spi.operator.tool.ToolCapabilityOperatorPermissions;
import tech.kayys.wayang.spi.operator.tool.ToolPermissionRequirement;
import tech.kayys.wayang.spi.operator.tool.ToolSummary;
import tech.kayys.wayang.spi.plugin.PluginState;
import tech.kayys.wayang.spi.sandbox.SandboxState;
import tech.kayys.wayang.spi.sandbox.SandboxType;
import tech.kayys.wayang.spi.sandbox.observability.SandboxHealthStatus;
import tech.kayys.wayang.spi.session.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class OperatorSpiPhase5Test {

    @Test
    void testPluginOperatorSpi() {
        PluginSummary summary = new PluginSummary(
                "p-1",
                "Plugin One",
                "1.0.0",
                "A test plugin",
                PluginState.ACTIVE,
                List.of("tech.kayys.ExtensionA")
        );
        assertEquals("p-1", summary.id());
        assertEquals(PluginState.ACTIVE, summary.state());
        assertEquals(1, summary.extensionTypes().size());

        assertNotNull(PluginOperatorPermissions.READ);
        assertNotNull(PluginOperatorPermissions.ENABLE);
        assertNotNull(PluginOperatorPermissions.DISABLE);
        assertNotNull(PluginOperatorPermissions.UNLOAD);
    }

    @Test
    void testSandboxOperatorSpi() {
        SandboxSummary summary = new SandboxSummary(
                "sb-1",
                "exec-1",
                "tenant-1",
                "agent-1",
                SandboxType.PROCESS,
                SandboxState.RUNNING,
                "process",
                Instant.now(),
                Instant.now(),
                Map.of("key", "val")
        );
        assertEquals("sb-1", summary.sandboxId());
        assertEquals(SandboxType.PROCESS, summary.type());

        SandboxHealthSummary health = new SandboxHealthSummary(
                "sb-1",
                SandboxHealthStatus.HEALTHY,
                Instant.now(),
                "OK",
                Map.of()
        );
        assertEquals(SandboxHealthStatus.HEALTHY, health.status());

        SandboxDiagnosticsSummary diag = new SandboxDiagnosticsSummary(
                "sb-1",
                Instant.now(),
                "RUNNING",
                "HEALTHY",
                List.of("warning-1"),
                Map.of()
        );
        assertEquals(1, diag.warnings().size());

        assertNotNull(SandboxOperatorPermissions.READ);
        assertNotNull(SandboxOperatorPermissions.HEALTH);
        assertNotNull(SandboxOperatorPermissions.METRICS);
        assertNotNull(SandboxOperatorPermissions.DIAGNOSTICS);
        assertNotNull(SandboxOperatorPermissions.STOP);
        assertNotNull(SandboxOperatorPermissions.DESTROY);
    }

    @Test
    void testToolCapabilityOperatorSpi() {
        CapabilitySummary cap = new CapabilitySummary(
                "cap-1",
                CapabilityType.of("tool", "browser"),
                "Web Browser",
                "Browser capability",
                "1.0.0",
                "prov-1",
                true,
                true,
                List.of("web", "browser"),
                Map.of()
        );
        assertEquals("cap-1", cap.id());
        assertTrue(cap.available());

        ToolPermissionRequirement req = ToolPermissionRequirement.required("filesystem.read");
        assertFalse(req.optional());

        ToolSummary tool = new ToolSummary(
                "file_reader",
                "Reads files",
                "1.0.0",
                "prov-1",
                List.of(req),
                List.of("wayang.filesystem"),
                Map.of()
        );
        assertEquals("file_reader", tool.name());
        assertEquals(1, tool.permissions().size());

        assertNotNull(ToolCapabilityOperatorPermissions.CAPABILITIES_READ);
        assertNotNull(ToolCapabilityOperatorPermissions.CAPABILITY_INSPECT);
        assertNotNull(ToolCapabilityOperatorPermissions.TOOLS_READ);
        assertNotNull(ToolCapabilityOperatorPermissions.TOOL_INSPECT);
    }

    @Test
    void testExecutionOperatorSpi() {
        ExecutionInfo info = new ExecutionInfo(
                "exec-1",
                "tenant-1",
                "user-1",
                "agent-1",
                "wf-1",
                ExecutionState.RUNNING,
                Instant.now(),
                Instant.now(),
                null,
                Instant.now(),
                "corr-1",
                null,
                null,
                Map.of()
        );
        assertEquals("exec-1", info.executionId());
        assertEquals(ExecutionState.RUNNING, info.state());
        assertTrue(info.started().isPresent());
        assertTrue(info.completed().isEmpty());

        ExecutionQuery query = ExecutionQuery.all();
        assertEquals(100, query.limit());

        ExecutionQuery custom = new ExecutionQuery(
                "t1", "u1", "a1", "w1",
                Set.of(ExecutionState.RUNNING),
                Instant.now().minusSeconds(60),
                Instant.now(),
                50
        );
        assertEquals(50, custom.limit());

        assertNotNull(ExecutionOperatorPermissions.READ);
        assertNotNull(ExecutionOperatorPermissions.INSPECT);
        assertNotNull(ExecutionOperatorPermissions.PAUSE);
        assertNotNull(ExecutionOperatorPermissions.RESUME);
        assertNotNull(ExecutionOperatorPermissions.CANCEL);
        assertNotNull(ExecutionOperatorPermissions.RETRY);
    }

    @Test
    void testSessionOperatorSpi() {
        SessionId id = SessionId.of("sess-1");
        assertEquals("sess-1", id.value());

        SessionInfo info = new SessionInfo(
                id,
                "t1",
                "u1",
                "a1",
                SessionState.ACTIVE,
                Instant.now(),
                Instant.now(),
                Instant.now().plusSeconds(3600),
                "corr-1",
                List.of("exec-1"),
                Map.of()
        );
        assertEquals(SessionState.ACTIVE, info.state());
        assertTrue(info.expires().isPresent());

        SessionQuery query = SessionQuery.all();
        assertEquals(100, query.limit());

        assertNotNull(SessionOperatorPermissions.READ);
        assertNotNull(SessionOperatorPermissions.INSPECT);
        assertNotNull(SessionOperatorPermissions.SUSPEND);
        assertNotNull(SessionOperatorPermissions.RESUME);
        assertNotNull(SessionOperatorPermissions.CLOSE);
    }

    @Test
    void testDiagnosticsOperatorSpi() {
        DiagnosticComponent comp = DiagnosticComponent.of(DiagnosticComponentType.SANDBOX, "sb-1");
        assertEquals(DiagnosticComponentType.SANDBOX, comp.type());
        assertEquals("sb-1", comp.id());

        DiagnosticIssue issue = DiagnosticIssue.warning("LIMIT_WARNING", "Memory at 85%");
        assertEquals(DiagnosticSeverity.WARNING, issue.severity());

        DiagnosticResult result = DiagnosticResult.degraded(comp, "Degraded performance", List.of(issue));
        assertEquals(DiagnosticStatus.DEGRADED, result.status());

        DiagnosticReport report = new DiagnosticReport(
                Instant.now(),
                DiagnosticStatus.DEGRADED,
                List.of(result),
                Map.of()
        );
        assertEquals(DiagnosticStatus.DEGRADED, report.overallStatus());

        DiagnosticContext ctx = DiagnosticContext.defaultContext();
        assertEquals(Duration.ofSeconds(5), ctx.timeout());

        assertNotNull(DiagnosticsOperatorPermissions.READ);
        assertNotNull(DiagnosticsOperatorPermissions.INSPECT);
        assertNotNull(DiagnosticsOperatorPermissions.PLATFORM);
    }
}
