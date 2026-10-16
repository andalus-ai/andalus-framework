
package tech.kayys.andalus.agent.kernel.interaction;

import tech.kayys.andalus.agent.kernel.execution.AgentExecutionController;

import java.util.Map;
import java.util.UUID;

public final class InteractionController {

    private final InteractionBus bus;
    private final ApprovalStore approvals;
    private final InputStore inputs;
    private final AgentExecutionController execution;

    public InteractionController(
            InteractionBus bus,
            ApprovalStore approvals,
            InputStore inputs,
            AgentExecutionController execution
    ) {
        this.bus = bus;
        this.approvals = approvals;
        this.inputs = inputs;
        this.execution = execution;
    }

    /** Backwards-compatible constructor without an input store. */
    public InteractionController(
            InteractionBus bus,
            ApprovalStore approvals,
            AgentExecutionController execution
    ) {
        this(bus, approvals, new InMemoryInputStore(), execution);
    }

    public InteractionEvent present(
            String runId,
            Presentation presentation
    ) {
        return bus.publish(
                runId,
                InteractionEventType.PRESENTATION,
                Map.of(
                        "presentation",
                        presentation
                )
        );
    }

    public ApprovalRequest requestApproval(
            String runId,
            String reason,
            String capability,
            Map<String, Object> context
    ) {
        String approvalId =
                UUID.randomUUID().toString();

        ApprovalRequest request =
                new ApprovalRequest(
                        approvalId,
                        runId,
                        reason,
                        capability,
                        context
                );

        approvals.save(request);

        execution.waitForApproval(
                runId,
                approvalId
        );

        bus.publish(
                runId,
                InteractionEventType.APPROVAL_REQUESTED,
                Map.of(
                        "approval",
                        request
                )
        );

        return request;
    }

    public ApprovalDecision grant(
            String approvalId,
            String actor
    ) {
        ApprovalRequest request =
                getPending(approvalId);

        ApprovalDecision decision =
                ApprovalDecision.granted(
                        approvalId,
                        request.runId(),
                        actor
                );

        approvals.decide(decision);

        bus.publish(
                request.runId(),
                InteractionEventType.APPROVAL_GRANTED,
                Map.of(
                        "approval",
                        decision
                )
        );

        execution.resume(
                request.runId()
        );

        return decision;
    }

    public ApprovalDecision reject(
            String approvalId,
            String actor,
            String reason
    ) {
        ApprovalRequest request =
                getPending(approvalId);

        ApprovalDecision decision =
                ApprovalDecision.rejected(
                        approvalId,
                        request.runId(),
                        actor,
                        reason
                );

        approvals.decide(decision);

        bus.publish(
                request.runId(),
                InteractionEventType.APPROVAL_REJECTED,
                Map.of(
                        "approval",
                        decision
                )
        );

        /*
         * Rejection does not automatically mean cancellation.
         * The AgentLoop may decide whether to:
         *
         * - retry
         * - choose another capability
         * - ask again
         * - terminate
         * - continue without the capability
         */
        execution.resume(
                request.runId()
        );

        return decision;
    }

    public void pause(
            String runId,
            String reason
    ) {
        execution.pause(
                runId,
                reason
        );
    }

    public void resume(
            String runId
    ) {
        execution.resume(
                runId
        );
    }

    public void cancel(
            String runId
    ) {
        execution.cancel(
                runId
        );
    }

    public InputRequest requestInput(
            String runId,
            String prompt,
            String inputType,
            Map<String, Object> schema,
            boolean required
    ) {
        String inputId =
                UUID.randomUUID().toString();

        InputRequest request =
                new InputRequest(
                        inputId,
                        runId,
                        prompt,
                        inputType,
                        schema,
                        required
                );

        inputs.save(request);

        execution.waitForInput(
                runId,
                inputId
        );

        bus.publish(
                runId,
                InteractionEventType.INPUT_REQUESTED,
                Map.of(
                        "input",
                        request
                )
        );

        return request;
    }

    public InputResponse answer(
            String inputId,
            Object value
    ) {
        InputRequest request =
                inputs.find(inputId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Unknown input: "
                                                + inputId
                                ));

        InputResponse response =
                InputResponse.answered(
                        inputId,
                        request.runId(),
                        value
                );

        inputs.submit(response);

        bus.publish(
                request.runId(),
                InteractionEventType.INPUT_RECEIVED,
                Map.of(
                        "input",
                        response
                )
        );

        execution.resume(
                request.runId()
        );

        return response;
    }

    private ApprovalRequest getPending(
            String approvalId
    ) {
        ApprovalRequest request =
                approvals.find(approvalId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Unknown approval: "
                                                + approvalId
                                ));

        if (!request.pending()) {
            throw new IllegalStateException(
                    "Approval is not pending: "
                            + approvalId
            );
        }

        if (request.expired()) {
            throw new IllegalStateException(
                    "Approval has expired: "
                            + approvalId
            );
        }

        return request;
    }
}
