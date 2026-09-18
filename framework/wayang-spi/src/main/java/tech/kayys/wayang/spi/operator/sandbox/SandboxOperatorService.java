package tech.kayys.wayang.spi.operator.sandbox;

import tech.kayys.wayang.spi.operator.OperatorContext;
import tech.kayys.wayang.spi.operator.OperatorResult;
import tech.kayys.wayang.spi.sandbox.Sandbox;

import java.util.List;

public interface SandboxOperatorService {

    OperatorResult<List<Sandbox>> list(
            OperatorContext context);

    OperatorResult<Sandbox> inspect(
            OperatorContext context,
            String sandboxId);

    OperatorResult<Void> destroy(
            OperatorContext context,
            String sandboxId);
}
