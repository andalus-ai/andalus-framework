package tech.kayys.andalus.spi.operator.session;

import tech.kayys.andalus.spi.operator.OperatorContext;
import tech.kayys.andalus.spi.operator.OperatorResult;
import tech.kayys.andalus.spi.session.SessionId;
import tech.kayys.andalus.spi.session.SessionInfo;
import tech.kayys.andalus.spi.session.SessionQuery;

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
