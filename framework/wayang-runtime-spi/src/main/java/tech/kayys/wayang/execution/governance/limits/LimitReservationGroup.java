package tech.kayys.wayang.execution.governance.limits;

import java.util.ArrayList;
import java.util.List;

public final class LimitReservationGroup implements LimitReservation {

    private final List<LimitReservation> reservations = new ArrayList<>();
    private boolean completed;

    public void add(LimitReservation reservation) {
        if (completed) {
            throw new IllegalStateException(
                    "Reservation group already completed"
            );
        }

        if (reservation != null) {
            reservations.add(reservation);
        }
    }

    @Override
    public synchronized void commit() {
        if (completed) {
            return;
        }

        for (LimitReservation reservation : reservations) {
            reservation.commit();
        }

        completed = true;
    }

    @Override
    public synchronized void rollback() {
        if (completed) {
            return;
        }

        for (int i = reservations.size() - 1; i >= 0; i--) {
            reservations.get(i).rollback();
        }

        completed = true;
    }

    @Override
    public synchronized boolean active() {
        return !completed;
    }
}
