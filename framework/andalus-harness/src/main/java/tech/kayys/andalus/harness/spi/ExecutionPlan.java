package tech.kayys.andalus.harness.spi;
import java.util.List;

/**
 * Defines the contract for execution plan operations in the Andalus framework.
 */

public interface ExecutionPlan {
    List<String> getSteps();
}
