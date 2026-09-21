package tech.kayys.andalus.spi.sandbox;

import java.util.List;

public interface SandboxProviderSelector {

    SandboxProvider select(
            SandboxRequest request,
            List<SandboxProvider> providers);
}
