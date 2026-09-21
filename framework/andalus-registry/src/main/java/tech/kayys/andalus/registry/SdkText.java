package tech.kayys.andalus.registry;

/**
 * Small text normalization helpers shared across registry models.
 */
final class SdkText {

    private SdkText() {
    }

    public static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    public static String blankToNull(String value) {
        String trimmed = trimToEmpty(value);
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String trimToDefault(String value, String defaultValue) {
        String trimmed = trimToEmpty(value);
        return trimmed.isEmpty() ? defaultValue : trimmed;
    }
}
