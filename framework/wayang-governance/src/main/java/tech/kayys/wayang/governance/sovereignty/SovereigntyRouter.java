package tech.kayys.wayang.governance.sovereignty;

/**
 * Service for verifying whether an execution workload or data transfer can legally cross to a target region.
 */
public interface SovereigntyRouter {

    /**
     * Determines whether execution can be dispatched to the target region under the given sovereignty policy.
     */
    boolean canDispatch(String workloadTag, SovereigntyPolicy policy, String targetRegion);
}
