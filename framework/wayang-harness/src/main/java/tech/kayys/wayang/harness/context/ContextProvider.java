package tech.kayys.wayang.harness.context;

public interface ContextProvider {

    ContextContribution provide(ContextRequest request);
}
