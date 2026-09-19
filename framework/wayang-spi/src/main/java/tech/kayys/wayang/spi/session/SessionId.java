package tech.kayys.wayang.spi.session;

import java.util.Objects;

public record SessionId(String value) {

    public SessionId {
        Objects.requireNonNull(value, "value");

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "session id must not be blank");
        }
    }

    public static SessionId of(String value) {
        return new SessionId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
