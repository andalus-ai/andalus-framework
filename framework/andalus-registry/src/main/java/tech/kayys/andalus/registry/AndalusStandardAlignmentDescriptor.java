package tech.kayys.andalus.registry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common identity descriptor for a protocol or standard alignment report.
 */
public record AndalusStandardAlignmentDescriptor(
        String standardId,
        String name,
        String version,
        String binding,
        String specUrl,
        Map<String, Object> attributes) {

    public AndalusStandardAlignmentDescriptor {
        standardId = SdkText.trimToDefault(standardId, "unknown");
        name = SdkText.trimToDefault(name, standardId);
        version = SdkText.trimToEmpty(version);
        binding = SdkText.trimToEmpty(binding);
        specUrl = SdkText.trimToEmpty(specUrl);
        attributes = attributes == null ? Map.of() : SdkMaps.orderedCopy(attributes);
    }

    public static AndalusStandardAlignmentDescriptor fromReportMap(Map<?, ?> report) {
        if (report == null || report.isEmpty()) {
            return new AndalusStandardAlignmentDescriptor("unknown", "unknown", "", "", "", Map.of());
        }
        Object sid = report.get("standardId");
        String standardId = sid != null ? String.valueOf(sid) : "unknown";
        Object n = report.get("name");
        String name = n != null ? String.valueOf(n) : standardId;
        Object v = report.get("version");
        String version = v != null ? String.valueOf(v) : "";
        Object b = report.get("binding");
        String binding = b != null ? String.valueOf(b) : "";
        Object u = report.get("specUrl");
        String specUrl = u != null ? String.valueOf(u) : "";
        return new AndalusStandardAlignmentDescriptor(standardId, name, version, binding, specUrl, Map.of());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("standardId", standardId);
        values.put("name", name);
        values.put("version", version);
        values.put("binding", binding);
        values.put("specUrl", specUrl);
        values.put("attributes", attributes);
        return SdkMaps.orderedCopy(values);
    }
}
