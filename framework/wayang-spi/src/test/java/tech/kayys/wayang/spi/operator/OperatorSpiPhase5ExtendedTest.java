package tech.kayys.wayang.spi.operator;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.spi.operator.audit.OperatorAuditEvent;
import tech.kayys.wayang.spi.operator.audit.OperatorAuditOutcome;
import tech.kayys.wayang.spi.operator.authorization.OperatorAuthorizationDecision;
import tech.kayys.wayang.spi.operator.authorization.OperatorAuthorizationRequest;
import tech.kayys.wayang.spi.operator.authorization.OperatorAuthorizationResult;
import tech.kayys.wayang.spi.operator.authorization.OperatorPermissionDefinition;
import tech.kayys.wayang.spi.operator.authorization.OperatorScope;
import tech.kayys.wayang.spi.operator.configuration.ConfigurationOperatorPermissions;
import tech.kayys.wayang.spi.operator.configuration.ConfigurationSummary;
import tech.kayys.wayang.spi.operator.configuration.ConfigurationUpdateRequest;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OperatorSpiPhase5ExtendedTest {

    @Test
    void testConfigurationPermissions() {
        assertEquals("operator.configuration.read", ConfigurationOperatorPermissions.READ.id());
        assertEquals("operator.configuration.inspect", ConfigurationOperatorPermissions.INSPECT.id());
        assertEquals("operator.configuration.update", ConfigurationOperatorPermissions.UPDATE.id());
        assertEquals("operator.configuration.activate", ConfigurationOperatorPermissions.ACTIVATE.id());
    }

    @Test
    void testConfigurationSummaryAndUpdateRequest() {
        var summary = new ConfigurationSummary(
                "cfg-1",
                "app-config",
                "FILE",
                "APPLICATION",
                "tenant-1",
                "ACTIVE",
                Map.of("key1", "val1"),
                Instant.now()
        );
        assertEquals("cfg-1", summary.id());
        assertEquals("app-config", summary.name());
        assertEquals("val1", summary.values().get("key1"));

        var updateReq = new ConfigurationUpdateRequest(
                Map.of("key2", "val2"),
                true
        );
        assertTrue(updateReq.activate());
        assertEquals("val2", updateReq.values().get("key2"));
    }

    @Test
    void testOperatorAuthorizationModels() {
        var context = new OperatorContext("t-1", "u-1", "c-1", "r-1", Map.of());
        var req = OperatorAuthorizationRequest.tenant(context, "operator.sandboxes.stop", "sandbox", "sb-1");
        assertEquals(OperatorScope.TENANT, req.scope());
        assertEquals("sandbox", req.resourceType());
        assertEquals("sb-1", req.resourceId());

        var platformReq = OperatorAuthorizationRequest.platform(context, "operator.plugins.enable", "plugin", "p-1");
        assertEquals(OperatorScope.PLATFORM, platformReq.scope());

        var allow = OperatorAuthorizationResult.allow(OperatorScope.TENANT);
        assertTrue(allow.allowed());
        assertEquals(OperatorAuthorizationDecision.ALLOW, allow.decision());

        var deny = OperatorAuthorizationResult.deny("DENIED", "Access denied");
        assertFalse(deny.allowed());
        assertEquals(OperatorAuthorizationDecision.DENY, deny.decision());
        assertEquals("DENIED", deny.code());
    }

    @Test
    void testOperatorPermissionDefinition() {
        var def = new OperatorPermissionDefinition(
                "operator.sandboxes.destroy",
                "Destroy a sandbox",
                OperatorScope.TENANT,
                true,
                true
        );
        assertEquals("operator.sandboxes.destroy", def.id());
        assertTrue(def.destructive());
        assertTrue(def.privileged());
    }

    @Test
    void testOperatorAuditEvent() {
        var event = new OperatorAuditEvent(
                "evt-1",
                Instant.now(),
                "t-1",
                "u-1",
                "corr-1",
                "req-1",
                "operator.execution.cancel",
                "operator.executions.cancel",
                OperatorScope.TENANT,
                "execution",
                "exec-100",
                true,
                true,
                OperatorAuditOutcome.SUCCESS,
                "AUTHORIZED",
                "Execution cancelled successfully",
                Map.of("state", "RUNNING"),
                Map.of("state", "CANCELLED"),
                Map.of("ip", "127.0.0.1")
        );
        assertEquals("evt-1", event.id());
        assertEquals(OperatorAuditOutcome.SUCCESS, event.outcome());
        assertTrue(event.destructive());
        assertEquals("CANCELLED", event.after().get("state"));
    }

    @Test
    void testOperatorAuthorizationException() {
        var ex = new OperatorAuthorizationException("SCOPE_MISMATCH", "Scope does not match");
        assertEquals("SCOPE_MISMATCH", ex.code());
        assertEquals("Scope does not match", ex.getMessage());
    }
}
