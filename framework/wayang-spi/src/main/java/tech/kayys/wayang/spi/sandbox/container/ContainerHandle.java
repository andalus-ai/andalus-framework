package tech.kayys.wayang.spi.sandbox.container;

import java.util.Objects;

public record ContainerHandle(
        String id,
        String name
) {
    public ContainerHandle {
        id = Objects.requireNonNull(id, "id");
        name = Objects.requireNonNull(name, "name");
    }
}
