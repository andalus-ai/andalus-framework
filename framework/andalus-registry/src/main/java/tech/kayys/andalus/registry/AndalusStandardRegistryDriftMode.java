package tech.kayys.andalus.registry;

import java.util.Locale;



/**
 * Deployment behavior for registry drift discovered in standard-alignment health.
 */
public enum AndalusStandardRegistryDriftMode {
    IGNORE,
    WARN,
    BLOCK;

    public static AndalusStandardRegistryDriftMode fromId(String id) {
        return switch (key(id)) {
            case "warn", "warning", "warnonly", "warningonly" -> WARN;
            case "block", "blocking", "fail", "failure", "required" -> BLOCK;
            default -> IGNORE;
        };
    }

    public String id() {
        return switch (this) {
            case IGNORE -> "ignore";
            case WARN -> "warn";
            case BLOCK -> "block";
        };
    }

    private static String key(String value) {
        return SdkText.trimToEmpty(value)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "");
    }
}
