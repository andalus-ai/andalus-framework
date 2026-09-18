package tech.kayys.wayang.harness.contract.protocol;

public enum TransportType {
    IN_PROCESS,
    UNIX_SOCKET,
    GRPC,
    HTTP,
    WEBSOCKET,
    MESSAGE_BUS
}
