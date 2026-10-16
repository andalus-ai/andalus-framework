package tech.kayys.andalus.a2ui.andalus.transport;

import tech.kayys.andalus.a2ui.andalus.WayangA2uiTransportOutcome;
import tech.kayys.andalus.a2ui.andalus.WayangA2uiTransportResponse;
import tech.kayys.andalus.a2ui.andalus.support.RecordCollections;

import java.util.List;
import java.util.Map;

/**
 * Shared transport aggregate metrics for A2UI exchange records.
 */
public final class TransportExchangeMetrics {

    public static long handledCount(List<? extends TransportMetricExchange> exchanges) {
        return exchanges(exchanges).stream()
                .map(TransportMetricExchange::transportResponse)
                .mapToLong(WayangA2uiTransportResponse::handledCount)
                .sum();
    }

    public static long rejectedCount(List<? extends TransportMetricExchange> exchanges) {
        return exchanges(exchanges).stream()
                .map(TransportMetricExchange::transportResponse)
                .mapToLong(WayangA2uiTransportResponse::rejectedCount)
                .sum();
    }

    public static boolean hasTransportErrors(List<? extends TransportMetricExchange> exchanges) {
        return exchanges(exchanges).stream().anyMatch(TransportMetricExchange::transportError);
    }

    public static List<WayangA2uiTransportOutcome> outcomes(List<? extends TransportMetricExchange> exchanges) {
        return exchanges(exchanges).stream()
                .map(TransportMetricExchange::outcome)
                .toList();
    }

    public static List<Map<String, Object>> responseEnvelopes(
            List<? extends TransportMetricExchange> exchanges) {
        return exchanges(exchanges).stream()
                .map(TransportMetricExchange::responseEnvelope)
                .toList();
    }

    private static List<? extends TransportMetricExchange> exchanges(
            List<? extends TransportMetricExchange> exchanges) {
        return RecordCollections.nonNullList(exchanges);
    }

    private TransportExchangeMetrics() {
    }
}
