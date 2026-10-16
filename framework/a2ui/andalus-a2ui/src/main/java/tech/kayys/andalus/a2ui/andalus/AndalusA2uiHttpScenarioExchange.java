package tech.kayys.andalus.a2ui.andalus;

import tech.kayys.andalus.a2ui.andalus.http.HttpMetricExchange;
import tech.kayys.andalus.a2ui.andalus.support.RecordNumbers;

import java.util.Objects;

/**
 * One HTTP request/response exchange captured during a scenario run.
 */
public record WayangA2uiHttpScenarioExchange(
        int index,
        WayangA2uiHttpRequest request,
        WayangA2uiHttpResponse response) implements HttpMetricExchange {

    public WayangA2uiHttpScenarioExchange {
        index = RecordNumbers.oneBased(index);
        request = Objects.requireNonNull(request, "request");
        response = Objects.requireNonNull(response, "response");
    }

    public int statusCode() {
        return response.statusCode();
    }

    public boolean successful() {
        return response.successful();
    }

    public WayangA2uiTransportResponse transportResponse() {
        return WayangA2uiTransportResponse.fromJson(response.body());
    }
}
