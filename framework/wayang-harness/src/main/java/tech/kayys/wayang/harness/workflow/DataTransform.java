package tech.kayys.wayang.harness.workflow;

/**
 * Functional transformation applied to data as it passes across a data-flow edge.
 */
@FunctionalInterface
public interface DataTransform {

    Object transform(Object input);

    static DataTransform identity() {
        return input -> input;
    }
}
