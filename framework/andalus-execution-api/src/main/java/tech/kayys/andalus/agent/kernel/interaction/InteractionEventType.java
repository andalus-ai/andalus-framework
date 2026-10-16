
package tech.kayys.andalus.agent.kernel.interaction;

/**
 * Events exposed by the Agent interaction layer.
 */
public enum InteractionEventType {

    /**
     * Agent produced something for presentation.
     */
    PRESENTATION,

    /**
     * UI state changed.
     */
    UI_STATE_CHANGED,

    /**
     * Human approval is required.
     */
    APPROVAL_REQUESTED,

    /**
     * Human granted an approval.
     */
    APPROVAL_GRANTED,

    /**
     * Human rejected an approval.
     */
    APPROVAL_REJECTED,

    /**
     * Agent requires structured human input.
     */
    INPUT_REQUESTED,

    /**
     * Human supplied requested input.
     */
    INPUT_RECEIVED,

    /**
     * Pause was requested.
     */
    PAUSE_REQUESTED,

    /**
     * Agent execution is actually paused.
     */
    PAUSED,

    /**
     * Resume was requested.
     */
    RESUME_REQUESTED,

    /**
     * Agent execution resumed.
     */
    RESUMED,

    /**
     * Cancellation requested.
     */
    CANCEL_REQUESTED,

    /**
     * Agent execution cancelled.
     */
    CANCELLED
}
