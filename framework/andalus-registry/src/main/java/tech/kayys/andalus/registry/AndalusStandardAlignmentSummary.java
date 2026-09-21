package tech.kayys.andalus.registry;

/**
 * Standard alignment summary entry.
 */
public record AndalusStandardAlignmentSummary(
        String standardId,
        AndalusStandardAlignmentDescriptor standard,
        int gapCount) {

    public AndalusStandardAlignmentSummary {
        standardId = SdkText.trimToDefault(standardId, "unknown");
    }
}
