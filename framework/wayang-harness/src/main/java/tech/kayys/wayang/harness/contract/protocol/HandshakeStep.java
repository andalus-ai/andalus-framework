package tech.kayys.wayang.harness.contract.protocol;

public enum HandshakeStep {
    WAYANG_HELLO,
    AGENT_HELLO,
    CONTRACT_OFFER,
    CONTRACT_ACCEPT,
    PROTOCOL_NEGOTIATE,
    READY,
    REJECT,
    INCOMPATIBLE
}
