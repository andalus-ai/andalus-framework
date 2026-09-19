package tech.kayys.wayang.spi.session;

import java.util.List;
import java.util.Optional;

public interface SessionManager {

    Optional<SessionInfo> find(SessionId sessionId);

    List<SessionInfo> list(SessionQuery query);

    void suspend(SessionId sessionId) throws Exception;

    void resume(SessionId sessionId) throws Exception;

    void close(SessionId sessionId) throws Exception;
}
