
package tech.kayys.andalus.agent.kernel.interaction;

/**
 * Protocol-neutral presentation formats.
 *
 * <p>The Agent Kernel does not depend on A2UI. A2UI is one
 * projection of the presentation model.</p>
 */
public enum PresentationFormat {

    TEXT,

    MARKDOWN,

    JSON,

    TABLE,

    IMAGE,

    CHART,

    ARTIFACT,

    /**
     * Presentation intended for the Andalus A2UI adapter.
     */
    A2UI
}
