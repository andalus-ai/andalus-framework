package tech.kayys.andalus.security.tenant;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TenantContextTest {

    @Test
    void emptyContextRepresentsMissingTenant() {
        TenantContext context = TenantContext.empty();

        assertFalse(context.hasTenant());
        assertNull(context.tenantId());
    }

    @Test
    void tenantFactoryRequiresNonBlankIdentifier() {
        assertThrows(NullPointerException.class, () -> TenantContext.of(null));
        assertThrows(IllegalArgumentException.class, () -> TenantContext.of(""));
        assertThrows(IllegalArgumentException.class, () -> TenantContext.of("   "));
    }

    @Test
    void canonicalConstructorRejectsBlankTenantIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> new TenantContext("\t", Map.of()));
    }

    @Test
    void presentTenantIsPreservedAsOpaqueIdentifier() {
        TenantContext context = TenantContext.of("tenant-A");

        assertTrue(context.hasTenant());
        assertEquals("tenant-A", context.tenantId());
    }

    @Test
    void attributesAreDefensivelyCopied() {
        Map<String, Object> source = new HashMap<>();
        source.put("region", "eu-west");

        TenantContext context = new TenantContext("tenant-A", source);
        source.put("region", "us-east");

        assertEquals("eu-west", context.attributes().get("region"));
        assertThrows(UnsupportedOperationException.class,
                () -> context.attributes().put("region", "ap-south"));
    }
}
