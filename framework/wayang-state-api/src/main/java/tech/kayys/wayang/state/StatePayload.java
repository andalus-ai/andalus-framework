package tech.kayys.wayang.state;

import java.util.Optional;

/**
 * Encapsulation of a state payload.
 */
public interface StatePayload {
    byte[] bytes();
    String contentType();
    <T> Optional<T> unwrap(Class<T> type);

    static StatePayload of(byte[] bytes, String contentType) {
        return new SimpleStatePayload(bytes, contentType, null);
    }

    static StatePayload of(byte[] bytes, String contentType, Object raw) {
        return new SimpleStatePayload(bytes, contentType, raw);
    }

    record SimpleStatePayload(byte[] bytes, String contentType, Object raw) implements StatePayload {
        @Override
        @SuppressWarnings("unchecked")
        public <T> Optional<T> unwrap(Class<T> type) {
            if (raw != null && type.isInstance(raw)) {
                return Optional.of((T) raw);
            }
            return Optional.empty();
        }
    }
}
