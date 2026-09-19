package tech.kayys.wayang.spi.operator.authorization;

import tech.kayys.wayang.spi.operator.OperatorContext;

import java.util.Set;

/**
 * Resolves identity, roles, and granted permissions from the operator context.
 */
public interface OperatorSubjectResolver {

    String subject(OperatorContext context);

    Set<String> roles(OperatorContext context);

    Set<String> permissions(OperatorContext context);
}
