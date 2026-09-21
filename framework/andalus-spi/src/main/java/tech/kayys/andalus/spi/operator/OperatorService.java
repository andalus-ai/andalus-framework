package tech.kayys.andalus.spi.operator;

public interface OperatorService {

    String id();

    String name();

    String description();

    default boolean enabled() {
        return true;
    }
}
