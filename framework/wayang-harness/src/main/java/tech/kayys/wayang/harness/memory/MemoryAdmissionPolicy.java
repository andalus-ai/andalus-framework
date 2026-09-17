package tech.kayys.wayang.harness.memory;

public interface MemoryAdmissionPolicy {

    boolean shouldAdmit(MemoryEntry candidate);

    static MemoryAdmissionPolicy allowAll() {
        return candidate -> candidate != null && !candidate.content().isBlank();
    }
}
