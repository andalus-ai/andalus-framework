
package tech.kayys.andalus.tool;

public final class ToolContextKeys {

    /**
     * Runtime-provided interaction context.
     *
     * <p>The value is intentionally Object-typed so the tool SPI
     * does not depend on the execution/interaction module.</p>
     */
    public static final String INTERACTION =
            "andalus.interaction";

    public static final String RUN_ID =
            "andalus.run.id";

    public static final String EXECUTION_ID =
            "andalus.execution.id";

    private ToolContextKeys() {
    }
}
