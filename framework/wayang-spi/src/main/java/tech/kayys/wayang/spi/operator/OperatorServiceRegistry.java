package tech.kayys.wayang.spi.operator;

import java.util.List;
import java.util.Optional;

public interface OperatorServiceRegistry {

    Optional<OperatorService> find(String id);

    List<OperatorService> findAll();

    void register(OperatorService service);

    Optional<OperatorService> unregister(String id);
}
