package tech.kayys.andalus.harness.model;

/**
 * Defines the contract for model stream operations in the Andalus framework.
 */


public interface ModelStream {

    void onToken(String token);

    void onEvent(ModelStreamEvent event);

    void onComplete(ModelResult result);

    void onError(Throwable throwable);
}
