package tech.kayys.wayang.spi.sandbox;

@FunctionalInterface
public interface IsolationResolver {

    IsolationDecision resolve(ExecutionIsolationContext context);
}
