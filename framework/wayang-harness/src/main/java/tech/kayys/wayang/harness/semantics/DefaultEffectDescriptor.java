package tech.kayys.wayang.harness.semantics;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable reference record implementing {@link EffectDescriptor}.
 */
public record DefaultEffectDescriptor(
        Set<EffectKind> kinds,
        EffectScope scope,
        Reversibility reversibility,
        Idempotency idempotency,
        ResourceImpact resourceImpact
) implements EffectDescriptor {

    public DefaultEffectDescriptor {
        kinds = kinds != null ? Set.copyOf(kinds) : Set.of(EffectKind.PURE);
        scope = scope != null ? scope : EffectScope.EXECUTION;
        reversibility = reversibility != null ? reversibility : Reversibility.PURE;
        idempotency = idempotency != null ? idempotency : Idempotency.PURE;
        resourceImpact = resourceImpact != null ? resourceImpact : ResourceImpact.minimal();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Set<EffectKind> kinds = EnumSet.noneOf(EffectKind.class);
        private EffectScope scope = EffectScope.EXECUTION;
        private Reversibility reversibility = Reversibility.PURE;
        private Idempotency idempotency = Idempotency.PURE;
        private ResourceImpact resourceImpact = ResourceImpact.minimal();

        public Builder kind(EffectKind kind) {
            this.kinds.add(Objects.requireNonNull(kind, "EffectKind cannot be null"));
            return this;
        }

        public Builder kinds(Set<EffectKind> kinds) {
            if (kinds != null) {
                this.kinds.addAll(kinds);
            }
            return this;
        }

        public Builder scope(EffectScope scope) {
            this.scope = Objects.requireNonNull(scope, "EffectScope cannot be null");
            return this;
        }

        public Builder reversibility(Reversibility reversibility) {
            this.reversibility = Objects.requireNonNull(reversibility, "Reversibility cannot be null");
            return this;
        }

        public Builder idempotency(Idempotency idempotency) {
            this.idempotency = Objects.requireNonNull(idempotency, "Idempotency cannot be null");
            return this;
        }

        public Builder resourceImpact(ResourceImpact impact) {
            this.resourceImpact = Objects.requireNonNull(impact, "ResourceImpact cannot be null");
            return this;
        }

        public DefaultEffectDescriptor build() {
            if (kinds.isEmpty()) {
                kinds.add(EffectKind.PURE);
            }
            return new DefaultEffectDescriptor(kinds, scope, reversibility, idempotency, resourceImpact);
        }
    }
}
