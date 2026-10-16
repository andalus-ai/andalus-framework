package tech.kayys.andalus.spi.capability;

import java.util.Set;

/**
 * Expands agent capability requirements through the capability graph.
 */
public interface CapabilityGraphResolver {

    CapabilityGraphResolution resolve(
            CapabilityRequirements requirements,
            CapabilityGraph graph
    );

    record CapabilityGraphResolution(
            Set<String> requested,
            Set<String> expanded,
            Set<String> required,
            Set<String> optional,
            Set<String> missing
    ) {

        public CapabilityGraphResolution {
            requested = requested == null
                    ? Set.of()
                    : Set.copyOf(requested);

            expanded = expanded == null
                    ? Set.of()
                    : Set.copyOf(expanded);

            required = required == null
                    ? Set.of()
                    : Set.copyOf(required);

            optional = optional == null
                    ? Set.of()
                    : Set.copyOf(optional);

            missing = missing == null
                    ? Set.of()
                    : Set.copyOf(missing);
        }

        public boolean satisfied() {
            return missing.isEmpty();
        }
    }
}
