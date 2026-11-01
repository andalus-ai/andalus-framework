package tech.kayys.andalus.security.delegation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DelegationConstraintsTest {

    @Test
    void missingConstraintsFailClosed() {
        DelegationConstraints constraints = new DelegationConstraints(null, null, 0);

        assertFalse(constraints.permitsCapability("database.read"));
        assertFalse(constraints.permitsAction("tool.invoke"));
        assertFalse(constraints.canDelegate());
    }

    @Test
    void childGrantCannotWidenParentCapabilitiesOrActions() {
        DelegationConstraints parent = new DelegationConstraints(
                List.of("documents.read", "documents.write"),
                List.of("read"),
                3);
        DelegationConstraints requested = new DelegationConstraints(
                List.of("*"),
                List.of("*"),
                10);

        DelegationConstraints child = parent.attenuate(requested);

        assertTrue(child.permitsCapability("documents.read"));
        assertTrue(child.permitsCapability("documents.write"));
        assertFalse(child.permitsCapability("payments.transfer"));
        assertTrue(child.permitsAction("read"));
        assertFalse(child.permitsAction("delete"));
        assertEquals(3, child.maxHops());
    }

    @Test
    void childGrantCanFurtherRestrictParent() {
        DelegationConstraints parent = new DelegationConstraints(
                List.of("*"), List.of("*"), 5);
        DelegationConstraints requested = new DelegationConstraints(
                List.of("search"), List.of("query"), 2);

        DelegationConstraints child = parent.attenuate(requested);

        assertTrue(child.permitsCapability("search"));
        assertFalse(child.permitsCapability("admin"));
        assertTrue(child.permitsAction("query"));
        assertFalse(child.permitsAction("delete"));
        assertEquals(2, child.maxHops());
    }

    @Test
    void exhaustedHopLimitCannotBeDecremented() {
        assertThrows(IllegalStateException.class,
                () -> DelegationConstraints.empty().decrementHop());
    }

    @Test
    void negativeHopLimitIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new DelegationConstraints(List.of("*"), List.of("*"), -1));
    }
}
