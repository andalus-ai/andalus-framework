package tech.kayys.wayang.spi.sandbox;

public record FilesystemMount(
        String source,
        String target,
        MountMode mode
) {

    public FilesystemMount {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException(
                    "source cannot be null or blank"
            );
        }

        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException(
                    "target cannot be null or blank"
            );
        }

        if (mode == null) {
            throw new IllegalArgumentException(
                    "mode cannot be null"
            );
        }

        source = source.trim();
        target = target.trim();
    }
}
