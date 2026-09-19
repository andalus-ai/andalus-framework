package tech.kayys.wayang.spi.operator.session;

import tech.kayys.wayang.spi.operator.OperatorContext;
import tech.kayys.wayang.spi.operator.OperatorResult;
import tech.kayys.wayang.spi.session.SessionId;
import tech.kayys.wayang.spi.session.SessionInfo;
import tech.kayys.wayang.spi.session.SessionQuery;

import java.util.List;

public interface SessionOperatorService {

    OperatorResult<List<SessionInfo>> list(
            OperatorContext context,
            SessionQuery query);

    OperatorResult<SessionInfo> inspect(
            OperatorContext context,
            SessionId sessionId);

    OperatorResult<Void> suspend(
            OperatorContext context,
            SessionId sessionId);

    OperatorResult<Void> resume(
            OperatorContext context,
            SessionId sessionId);

    OperatorResult<Void> close(
            OperatorContext context,
            SessionId sessionId);
}
