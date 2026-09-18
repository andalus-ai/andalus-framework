package tech.kayys.wayang.spi.operator;

public interface OperatorService {

    String id();

    String name();

    String description();

    default boolean enabled() {
        return true;
    }
}
