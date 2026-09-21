package tech.kayys.andalus.spi.sandbox.vm;

import tech.kayys.andalus.spi.sandbox.Sandbox;

public interface VMSandbox extends Sandbox {

    String vmId();

    String image();
}
