package tech.kayys.wayang.harness.model;

public interface ModelStream {

    void onToken(String token);

    void onEvent(ModelStreamEvent event);

    void onComplete(ModelResult result);

    void onError(Throwable throwable);
}
