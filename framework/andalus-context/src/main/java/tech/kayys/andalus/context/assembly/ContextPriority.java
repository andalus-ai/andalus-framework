package tech.kayys.andalus.context.assembly;

/**
 * Priority classification for context contributions during budgeting and assembly.
 */
public enum ContextPriority {
    CRITICAL(100),
    HIGH(75),
    NORMAL(50),
    LOW(25),
    OPTIONAL(10);

    private final int rank;

    ContextPriority(int rank) {
        this.rank = rank;
    }

    public int rank() {
        return rank;
    }
}
