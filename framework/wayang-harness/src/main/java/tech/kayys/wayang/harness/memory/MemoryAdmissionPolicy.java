package tech.kayys.wayang.harness.memory;

/**
 * Defines the contract for memory admission policy operations in the Wayang framework.
 */


public interface MemoryAdmissionPolicy {

    boolean shouldAdmit(MemoryEntry candidate);

    static MemoryAdmissionPolicy allowAll() {
        return candidate -> candidate != null && !candidate.content().isBlank();
    }
}
