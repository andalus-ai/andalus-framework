package tech.kayys.andalus.spi.sandbox;

@FunctionalInterface
public interface IsolationResolver {

    IsolationDecision resolve(ExecutionIsolationContext context);
}
