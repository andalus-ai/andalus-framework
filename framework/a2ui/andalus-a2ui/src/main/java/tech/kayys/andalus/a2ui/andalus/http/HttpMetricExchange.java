package tech.kayys.andalus.a2ui.andalus.http;

import tech.kayys.andalus.a2ui.andalus.transport.TransportMetricExchange;

/**
 * Minimal metric-facing view shared by A2UI HTTP exchange records.
 */
public interface HttpMetricExchange extends TransportMetricExchange {

    int statusCode();

    boolean successful();
}
