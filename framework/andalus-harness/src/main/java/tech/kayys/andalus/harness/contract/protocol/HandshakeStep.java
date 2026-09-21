package tech.kayys.andalus.harness.contract.protocol;

public enum HandshakeStep {
    ANDALUS_HELLO,
    AGENT_HELLO,
    CONTRACT_OFFER,
    CONTRACT_ACCEPT,
    PROTOCOL_NEGOTIATE,
    READY,
    REJECT,
    INCOMPATIBLE
}
