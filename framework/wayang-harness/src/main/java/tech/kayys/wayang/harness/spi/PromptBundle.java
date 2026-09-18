package tech.kayys.wayang.harness.spi;
import java.util.List;

/**
 * Defines the contract for prompt bundle operations in the Wayang framework.
 */

public interface PromptBundle {
    List<String> getSystemPrompts();
    List<String> getUserPrompts();
}
