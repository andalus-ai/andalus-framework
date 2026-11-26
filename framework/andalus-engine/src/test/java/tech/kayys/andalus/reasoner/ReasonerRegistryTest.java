package tech.kayys.andalus.reasoner;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReasonerRegistryTest {

    @Test
    void resolvesProviderForDeclaredStrategy() {
        Reasoner react = provider(Set.of(ReasoningStrategy.REACT));

        ReasonerRegistry registry = new ReasonerRegistry(List.of(react));

        assertSame(react, registry.require(ReasoningStrategy.REACT));
        assertEquals(Set.of(ReasoningStrategy.REACT), registry.supportedStrategies());
    }

    @Test
    void returnsEmptyWhenStrategyIsNotRegistered() {
        ReasonerRegistry registry = new ReasonerRegistry(
                List.of(provider(Set.of(ReasoningStrategy.REACT))));

        assertTrue(registry.find(ReasoningStrategy.REFLEXION).isEmpty());
    }

    @Test
    void requireExplainsMissingStrategy() {
        ReasonerRegistry registry = new ReasonerRegistry(List.of());

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> registry.require(ReasoningStrategy.REACT));

        assertTrue(error.getMessage().contains("REACT"));
    }

    @Test
    void rejectsAmbiguousStrategyRegistrations() {
        Reasoner first = provider(Set.of(ReasoningStrategy.REACT));
        Reasoner second = provider(Set.of(ReasoningStrategy.REACT));

        assertThrows(IllegalArgumentException.class,
                () -> new ReasonerRegistry(List.of(first, second)));
    }

    @Test
    void allowsOneProviderToSupportMultipleStrategies() {
        Reasoner provider = provider(Set.of(
                ReasoningStrategy.REACT, ReasoningStrategy.REFLEXION));

        ReasonerRegistry registry = new ReasonerRegistry(List.of(provider));

        assertSame(provider, registry.require(ReasoningStrategy.REACT));
        assertSame(provider, registry.require(ReasoningStrategy.REFLEXION));
    }

    @Test
    void rejectsProviderWithoutDeclaredStrategies() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReasonerRegistry(List.of(provider(Set.of()))));
    }

    private static Reasoner provider(Set<ReasoningStrategy> strategies) {
        return (Reasoner) Proxy.newProxyInstance(
                Reasoner.class.getClassLoader(),
                new Class<?>[]{Reasoner.class},
                (proxy, method, args) -> {
                    if ("getSupportedStrategies".equals(method.getName())) {
                        return strategies;
                    }
                    if ("toString".equals(method.getName())) {
                        return "TestReasoner" + strategies;
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
