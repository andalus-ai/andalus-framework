package tech.kayys.andalus.command;

import java.util.List;

import tech.kayys.andalus.workbench.WorkbenchCommand;
import tech.kayys.andalus.workbench.WorkbenchCommandDiscovery;
import tech.kayys.andalus.workbench.WorkbenchCommandIndex;
import tech.kayys.andalus.workbench.WorkbenchCommandQuery;
import tech.kayys.andalus.client.SdkLists;
import tech.kayys.andalus.client.AndalusWorkbenchModel;

public final class AndalusCommandDiscoveryService {

    private static final AndalusCommandDiscoveryService INSTANCE = new AndalusCommandDiscoveryService();

    private AndalusCommandDiscoveryService() {
    }

    public static AndalusCommandDiscoveryService create() {
        return INSTANCE;
    }

    public List<WorkbenchCommand> discover(AndalusWorkbenchModel workbench, WorkbenchCommandQuery query) {
        return discover(workbench == null ? List.of() : workbench.commands(), query);
    }

    public WorkbenchCommandDiscovery commandDiscovery(AndalusWorkbenchModel workbench, WorkbenchCommandQuery query) {
        return commandDiscovery(workbench == null ? List.of() : workbench.commands(), query);
    }

    public WorkbenchCommandDiscovery commandDiscovery(List<WorkbenchCommand> commands, WorkbenchCommandQuery query) {
        WorkbenchCommandQuery normalized = query == null ? WorkbenchCommandQuery.all() : query;
        List<WorkbenchCommand> source = SdkLists.copy(commands);
        return WorkbenchCommandDiscovery.of(normalized, discover(source, normalized), source.size());
    }

    public AndalusWorkbenchModel filterWorkbench(AndalusWorkbenchModel workbench, WorkbenchCommandQuery query) {
        AndalusWorkbenchModel source = workbench == null ? new AndalusWorkbenchModel(null, List.of(), List.of(), List.of()) : workbench;
        WorkbenchCommandQuery normalized = query == null ? WorkbenchCommandQuery.all() : query;
        if (!normalized.filtered()) {
            return source;
        }
        return new AndalusWorkbenchModel(
                source.status(),
                source.productSurfaces(),
                List.of(),
                discover(source.commands(), normalized),
                source.nextActions());
    }

    public List<WorkbenchCommand> discover(List<WorkbenchCommand> commands, WorkbenchCommandQuery query) {
        WorkbenchCommandQuery normalized = query == null ? WorkbenchCommandQuery.all() : query;
        return WorkbenchCommandIndex.of(commands).commandsForQuery(normalized);
    }
}
