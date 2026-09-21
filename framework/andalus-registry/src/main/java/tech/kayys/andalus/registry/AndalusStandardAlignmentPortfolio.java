package tech.kayys.andalus.registry;

import java.util.List;

/**
 * Standard alignment portfolio.
 */
public record AndalusStandardAlignmentPortfolio(
        List<AndalusStandardAlignmentSummary> standards) {

    public AndalusStandardAlignmentPortfolio {
        standards = standards == null ? List.of() : List.copyOf(standards);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<AndalusStandardAlignmentSummary> standards = List.of();

        public Builder standards(List<AndalusStandardAlignmentSummary> standards) {
            this.standards = standards;
            return this;
        }

        public AndalusStandardAlignmentPortfolio build() {
            return new AndalusStandardAlignmentPortfolio(standards);
        }
    }
}
