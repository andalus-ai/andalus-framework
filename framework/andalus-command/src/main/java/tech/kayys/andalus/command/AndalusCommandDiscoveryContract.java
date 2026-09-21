package tech.kayys.andalus.command;

import tech.kayys.andalus.client.SdkText;

public record AndalusCommandDiscoveryContract(
        String schema,
        int version,
        String envelope) {

    public static final String SCHEMA = "andalus.command.discovery";
    public static final int VERSION = 1;
    public static final String COMMANDS_DISCOVERY = "commands-discovery";

    public AndalusCommandDiscoveryContract {
        schema = SdkText.trimToDefault(schema, SCHEMA);
        version = Math.max(1, version);
        envelope = SdkText.trimToEmpty(envelope);
    }

    public static AndalusCommandDiscoveryContract commandsDiscovery() {
        return new AndalusCommandDiscoveryContract(SCHEMA, VERSION, COMMANDS_DISCOVERY);
    }
}
