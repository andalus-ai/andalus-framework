package tech.kayys.andalus.planner;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


/**
 * Planning Strategies
 */
public enum PlanningStrategy {
    SEQUENTIAL,
    TREE_SEARCH,
    REACT,
    PLAN_EXECUTE,
    LLM_PLANNER,
    WORKFLOW,
    BPMN,
    TEMPORAL,
    AI_PLANNING,
    HUMAN_PLANNER
}
