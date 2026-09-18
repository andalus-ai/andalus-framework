package tech.kayys.wayang.spi.sandbox.vm;

import tech.kayys.wayang.spi.sandbox.Sandbox;

public interface VMSandbox extends Sandbox {

    String vmId();

    String image();
}
