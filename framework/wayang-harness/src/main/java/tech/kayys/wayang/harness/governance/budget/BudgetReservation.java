package tech.kayys.wayang.harness.governance.budget;

public interface BudgetReservation {

    String id();

    BudgetRequest request();

    BudgetAmount reserved();

    boolean active();
}
