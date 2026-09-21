package tech.kayys.andalus.harness.authority;

/**
 * States of a coordinator node in an HA cluster.
 */
public enum LeadershipState {
    STARTING,
    CANDIDATE,
    LEADER,
    DRAINING,
    FOLLOWER,
    OFFLINE;

    public boolean isLeader() {
        return this == LEADER;
    }
}
