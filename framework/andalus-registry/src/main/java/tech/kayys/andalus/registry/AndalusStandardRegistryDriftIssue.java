package tech.kayys.andalus.registry;

import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Field-level mismatch between a reported standard descriptor and the SDK registry.
 */
public record AndalusStandardRegistryDriftIssue(
        String standardId,
        String field,
        String expected,
        String actual) {

    public AndalusStandardRegistryDriftIssue {
        standardId = SdkText.trimToDefault(standardId, "unknown");
        field = SdkText.trimToDefault(field, "unknown");
        expected = SdkText.trimToEmpty(expected);
        actual = SdkText.trimToEmpty(actual);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("standardId", standardId);
        values.put("field", field);
        values.put("expected", expected);
        values.put("actual", actual);
        return SdkMaps.orderedCopy(values);
    }
}
