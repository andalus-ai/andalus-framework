
package tech.kayys.andalus.agent.kernel.interaction;

public record InteractionContext(
        String runId,
        InteractionController controller
) {

    public ApprovalRequest requestApproval(
            String reason,
            String capability,
            java.util.Map<String, Object> context
    ) {
        return controller.requestApproval(
                runId,
                reason,
                capability,
                context
        );
    }

    public InputRequest requestInput(
            String prompt,
            String inputType,
            java.util.Map<String, Object> schema,
            boolean required
    ) {
        return controller.requestInput(
                runId,
                prompt,
                inputType,
                schema,
                required
        );
    }

    public void present(
            Presentation presentation
    ) {
        controller.present(
                runId,
                presentation
        );
    }
}
