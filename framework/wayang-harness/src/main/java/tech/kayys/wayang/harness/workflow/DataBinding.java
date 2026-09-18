package tech.kayys.wayang.harness.workflow;

import java.util.Objects;

/**
 * Data binding configuration connecting the output of a source node to the input of a target node.
 */
public record DataBinding(
        DataReference source,
        DataReference target,
        DataTransform transform,
        DataContract contract
) {

    public DataBinding {
        Objects.requireNonNull(source, "source reference cannot be null");
        Objects.requireNonNull(target, "target reference cannot be null");
        transform = transform != null ? transform : DataTransform.identity();
        contract = contract != null ? contract : DataContract.standard("application/octet-stream");
    }

    public static DataBinding of(DataReference source, DataReference target) {
        return new DataBinding(source, target, DataTransform.identity(), DataContract.standard("application/octet-stream"));
    }

    public static DataBinding of(DataReference source, DataReference target, DataContract contract) {
        return new DataBinding(source, target, DataTransform.identity(), contract);
    }
}
