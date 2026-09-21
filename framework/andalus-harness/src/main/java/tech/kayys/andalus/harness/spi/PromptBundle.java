package tech.kayys.andalus.harness.spi;
import java.util.List;

/**
 * Defines the contract for prompt bundle operations in the Andalus framework.
 */

public interface PromptBundle {
    List<String> getSystemPrompts();
    List<String> getUserPrompts();
}
