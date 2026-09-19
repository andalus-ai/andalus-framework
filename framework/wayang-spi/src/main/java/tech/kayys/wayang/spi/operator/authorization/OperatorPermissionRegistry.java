package tech.kayys.wayang.spi.operator.authorization;

import java.util.List;
import java.util.Optional;

/**
 * Registry of available operator permissions and their security metadata.
 */
public interface OperatorPermissionRegistry {

    void register(OperatorPermissionDefinition permission);

    Optional<OperatorPermissionDefinition> find(String permissionId);

    List<OperatorPermissionDefinition> list();
}
