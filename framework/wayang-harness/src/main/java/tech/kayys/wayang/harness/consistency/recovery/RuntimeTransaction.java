package tech.kayys.wayang.harness.consistency.recovery;

/**
 * Interface representing a scoped, localized transaction boundary.
 */
public interface RuntimeTransaction {

    TransactionId id();

    TransactionState state();

    void commit();

    void rollback();

    void compensate();
}
