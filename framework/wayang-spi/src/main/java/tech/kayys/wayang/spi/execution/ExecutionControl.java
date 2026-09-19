package tech.kayys.wayang.spi.execution;

import java.util.List;
import java.util.Optional;

public interface ExecutionControl {

    Optional<ExecutionInfo> find(String executionId);

    List<ExecutionInfo> list(ExecutionQuery query);

    void pause(String executionId) throws Exception;

    void resume(String executionId) throws Exception;

    void cancel(String executionId) throws Exception;

    void retry(String executionId) throws Exception;
}
