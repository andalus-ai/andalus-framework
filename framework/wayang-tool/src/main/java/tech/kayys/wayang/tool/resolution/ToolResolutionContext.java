package tech.kayys.wayang.tool.resolution;

public record ToolResolutionContext(
        Object identity,
        Object resources,
        Object policy
) {
    public static ToolResolutionContext of(Object identity, Object resources) {
        return new ToolResolutionContext(identity, resources, null);
    }

    public static ToolResolutionContext empty() {
        return new ToolResolutionContext(null, null, null);
    }
}
