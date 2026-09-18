package tech.kayys.wayang.harness.spi;
import java.util.List;

/**
 * Defines the contract for execution plan operations in the Wayang framework.
 */

public interface ExecutionPlan {
    List<String> getSteps();
}
