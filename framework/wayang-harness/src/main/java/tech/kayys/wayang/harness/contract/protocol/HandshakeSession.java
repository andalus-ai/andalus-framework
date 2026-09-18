package tech.kayys.wayang.harness.contract.protocol;

import tech.kayys.wayang.harness.contract.AgentContract;
import tech.kayys.wayang.harness.contract.compatibility.CompatibilityResult;
import tech.kayys.wayang.harness.protocol.AgentRef;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manages the protocol negotiation handshake sequence between Wayang and an agent.
 */
public class HandshakeSession {

    private HandshakeStep currentStep = HandshakeStep.WAYANG_HELLO;
    private AgentContract negotiatedContract;
    private Set<ProtocolFeature> negotiatedFeatures = Set.of();

    public HandshakeStep step() {
        return currentStep;
    }

    public AgentContract contract() {
        return negotiatedContract;
    }

    public Set<ProtocolFeature> features() {
        return negotiatedFeatures;
    }

    public HandshakeStep onAgentHello(AgentRef agentRef) {
        if (currentStep == HandshakeStep.WAYANG_HELLO) {
            currentStep = HandshakeStep.CONTRACT_OFFER;
        }
        return currentStep;
    }

    public HandshakeStep onContractOffer(AgentContract offer, ProtocolContract harnessProtocol) {
        Objects.requireNonNull(offer, "offer");
        Objects.requireNonNull(harnessProtocol, "harnessProtocol");

        CompatibilityResult result = offer.compatibility().evaluate(offer);
        if (!result.compatible()) {
            currentStep = HandshakeStep.INCOMPATIBLE;
            return currentStep;
        }

        // Intersect protocol features
        Set<ProtocolFeature> commonFeatures = offer.protocol().features().stream()
                .filter(harnessProtocol::supportsFeature)
                .collect(Collectors.toSet());

        this.negotiatedContract = offer;
        this.negotiatedFeatures = Set.copyOf(commonFeatures);
        this.currentStep = HandshakeStep.READY;
        return currentStep;
    }
}
