package tech.kayys.wayang.harness.contract.protocol;

import tech.kayys.wayang.harness.contract.version.ContractVersion;
import java.util.Set;

/**
 * Protocol contract defining communication protocols, transports, and features.
 */
public record ProtocolContract(
        ContractVersion protocolVersion,
        Set<TransportType> supportedTransports,
        Set<ProtocolFeature> features
) {
    public ProtocolContract {
        supportedTransports = supportedTransports != null ? Set.copyOf(supportedTransports) : Set.of();
        features = features != null ? Set.copyOf(features) : Set.of();
    }

    public static ProtocolContract standard(ContractVersion version) {
        return new ProtocolContract(
                version,
                Set.of(TransportType.IN_PROCESS, TransportType.GRPC),
                Set.of(ProtocolFeature.STREAMING, ProtocolFeature.CANCELLATION, ProtocolFeature.PROGRESS_REPORTING)
        );
    }

    public boolean supportsFeature(ProtocolFeature feature) {
        return features.contains(feature);
    }
}
