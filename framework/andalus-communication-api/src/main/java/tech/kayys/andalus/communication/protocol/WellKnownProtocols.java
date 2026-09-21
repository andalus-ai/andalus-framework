package tech.kayys.andalus.communication.protocol;

public final class WellKnownProtocols {

    private WellKnownProtocols() {
    }

    public static final ProtocolId IN_PROCESS =
            new ProtocolId("andalus.inprocess");

    public static final ProtocolId A2A =
            new ProtocolId("a2a");

    public static final ProtocolId ANP =
            new ProtocolId("anp");
}
