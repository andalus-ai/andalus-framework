package tech.kayys.wayang.harness.model;

/**
 * Defines the contract for model stream operations in the Wayang framework.
 */


public interface ModelStream {

    void onToken(String token);

    void onEvent(ModelStreamEvent event);

    void onComplete(ModelResult result);

    void onError(Throwable throwable);
}
