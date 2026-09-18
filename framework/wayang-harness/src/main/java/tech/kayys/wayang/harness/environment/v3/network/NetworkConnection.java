package tech.kayys.wayang.harness.environment.v3.network;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handle representing an active governed network connection.
 */
public interface NetworkConnection extends AutoCloseable {

    String endpoint();

    boolean isConnected();

    InputStream inputStream();

    OutputStream outputStream();

    @Override
    void close();
}
