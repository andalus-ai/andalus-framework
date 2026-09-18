package tech.kayys.wayang.execution.governance.limits;

import java.math.BigDecimal;

public interface BudgetController {

    LimitCheckResult tryReserve(
            LimitDefinition definition,
            LimitContext context,
            BigDecimal amount);

    void commit(
            LimitDefinition definition,
            LimitContext context,
            BigDecimal amount);

    void release(
            LimitDefinition definition,
            LimitContext context,
            BigDecimal amount);
}
