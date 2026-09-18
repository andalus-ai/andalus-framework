package tech.kayys.wayang.harness.security.identity;

import java.util.Objects;
import java.util.Set;

/**
 * Universal security principal representing an actor (Agent, User, System) in the Harness.
 */
public interface Principal {

    String id();

    PrincipalType type();

    Set<String> roles();

    enum PrincipalType {
        AGENT,
        USER,
        SERVICE,
        SYSTEM
    }

    record AgentPrincipal(String id, Set<String> roles) implements Principal {
        public AgentPrincipal {
            Objects.requireNonNull(id, "id");
            roles = roles != null ? Set.copyOf(roles) : Set.of();
        }

        @Override
        public PrincipalType type() {
            return PrincipalType.AGENT;
        }
    }

    record UserPrincipal(String id, Set<String> roles) implements Principal {
        public UserPrincipal {
            Objects.requireNonNull(id, "id");
            roles = roles != null ? Set.copyOf(roles) : Set.of();
        }

        @Override
        public PrincipalType type() {
            return PrincipalType.USER;
        }
    }
}
