package tech.kayys.andalus.spi.operator.execution;

import tech.kayys.andalus.spi.execution.ExecutionInfo;
import tech.kayys.andalus.spi.execution.ExecutionQuery;
import tech.kayys.andalus.spi.operator.OperatorContext;
import tech.kayys.andalus.spi.operator.OperatorResult;

import java.util.List;

public interface ExecutionOperatorService {

    OperatorResult<List<ExecutionInfo>> list(
            OperatorContext context,
            ExecutionQuery query);

    OperatorResult<ExecutionInfo> inspect(
            OperatorContext context,
            String executionId);

    OperatorResult<Void> pause(
            OperatorContext context,
            String executionId);

    OperatorResult<Void> resume(
            OperatorContext context,
            String executionId);

    OperatorResult<Void> cancel(
            OperatorContext context,
            String executionId);

    OperatorResult<Void> retry(
            OperatorContext context,
            String executionId);
}
