package tech.kayys.andalus.harness.coordination;

/**
 * The synchronization action startup reconciliation requires for one Gamelan dispatch
 * (6.17.3.11–6.17.3.16).
 *
 * <p>The reconciler decides <em>what</em> must happen; the caller schedules it. Nothing
 * here executes agents, retries nodes, checkpoints, cancels, or runs workflows
 * (6.17.3.29).</p>
 */
public enum GamelanReconciliationAction {

    /** Binding active, Claw execution alive and non-terminal — leave everything alone. */
    KEEP_WAITING,

    /** Claw execution already terminal — notify Gamelan so it completes the node. */
    NOTIFY_COMPLETED,

    /** Claw execution failed — notify Gamelan (it owns node retry policy). */
    NOTIFY_FAILED,

    /** Claw execution cancelled — notify Gamelan. */
    NOTIFY_CANCELLED,

    /** Binding exists but its Claw execution is gone (lost worker) — Gamelan must decide. */
    MARK_LOST,

    /** Gamelan is dispatching a node with no Claw binding — Claw must bind/rebind it. */
    REBIND_REQUIRED,

    /** A newer Gamelan attempt owns this node; this dispatch is historical. */
    STALE_DISPATCH
}
