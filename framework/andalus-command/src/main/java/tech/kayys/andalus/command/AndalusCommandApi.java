package tech.kayys.andalus.command;

import java.util.List;
import java.util.Map;

import tech.kayys.andalus.client.Andalus;
import tech.kayys.andalus.client.AndalusAqliSdk;
import tech.kayys.andalus.client.AndalusWireApi;
import tech.kayys.andalus.client.AndalusWorkbenchModel;
import tech.kayys.andalus.workbench.WorkbenchCommand;
import tech.kayys.andalus.workbench.WorkbenchCommandDiscovery;
import tech.kayys.andalus.workbench.WorkbenchCommandEnvelopes;
import tech.kayys.andalus.workbench.WorkbenchCommandQuery;

/**
 * Command discovery API for workbench, command palette, and wrapper integrations.
 *
 * <p>The API delegates discovery to the configured SDK instance and owns the
 * command JSON envelope boundary through {@link WorkbenchCommandEnvelopes}.</p>
 */
public final class AndalusCommandApi {

    private final AndalusAqliSdk sdk;
    private final AndalusWireApi wire;

    public AndalusCommandApi(AndalusAqliSdk sdk, AndalusWireApi wire) {
        this.sdk = sdk == null ? Andalus.local() : sdk;
        this.wire = wire == null ? new AndalusWireApi() : wire;
    }

    public AndalusWorkbenchModel workbench(WorkbenchCommandQuery query) {
        return sdk.workbench(query);
    }

    public WorkbenchCommandDiscovery discover(WorkbenchCommandQuery query) {
        return sdk.commandDiscovery(query);
    }

    public WorkbenchCommandDiscovery discover(String surfaceId, String category, String commandId) {
        return discover(WorkbenchCommandQuery.of(surfaceId, category, commandId));
    }

    public List<WorkbenchCommand> list(WorkbenchCommandQuery query) {
        return discover(query).commands();
    }

    public Map<String, Object> discoveryEnvelope(WorkbenchCommandQuery query) {
        return discoveryEnvelope(discover(query));
    }

    public Map<String, Object> discoveryEnvelope(WorkbenchCommandDiscovery discovery) {
        return WorkbenchCommandEnvelopes.discovery(productName(), discovery);
    }

    public Map<String, Object> indexEnvelope(WorkbenchCommandQuery query) {
        return indexEnvelope(discover(query));
    }

    public Map<String, Object> indexEnvelope(WorkbenchCommandDiscovery discovery) {
        return WorkbenchCommandEnvelopes.index(productName(), discovery);
    }

    public Map<String, Object> workbenchEnvelope(WorkbenchCommandQuery query) {
        return workbenchEnvelope(workbench(query), query);
    }

    public Map<String, Object> workbenchEnvelope(AndalusWorkbenchModel model, WorkbenchCommandQuery query) {
        return WorkbenchCommandEnvelopes.workbench(model, sdk.productSurfacePolicies(), query);
    }

    public String discoveryJson(WorkbenchCommandQuery query) {
        return discoveryJson(discover(query));
    }

    public String discoveryJson(WorkbenchCommandDiscovery discovery) {
        return wire.object(discoveryEnvelope(discovery));
    }

    public String indexJson(WorkbenchCommandQuery query) {
        return indexJson(discover(query));
    }

    public String indexJson(WorkbenchCommandDiscovery discovery) {
        return wire.object(indexEnvelope(discovery));
    }

    public String workbenchJson(WorkbenchCommandQuery query) {
        return workbenchJson(workbench(query), query);
    }

    public String workbenchJson(AndalusWorkbenchModel model, WorkbenchCommandQuery query) {
        return wire.object(workbenchEnvelope(model, query));
    }

    private String productName() {
        return sdk.status().productName();
    }
}
