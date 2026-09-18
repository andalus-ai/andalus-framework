package tech.kayys.wayang.harness.controlplane;

import org.junit.jupiter.api.Test;
import tech.kayys.wayang.harness.authority.CoordinationEpoch;
import tech.kayys.wayang.harness.journal.EventSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ControlPlaneTest {

    @Test
    void testEventPublishAndSubscription() {
        ControlPlaneEventBus bus = new DefaultControlPlaneEventBus();
        StreamId stream = StreamId.of("execution-stream-1");

        List<EventEnvelope> receivedStreamEvents = new ArrayList<>();
        List<EventEnvelope> receivedGlobalEvents = new ArrayList<>();

        EventSubscription sub1 = bus.subscribe(stream, receivedStreamEvents::add);
        EventSubscription subGlobal = bus.subscribeAll(receivedGlobalEvents::add);

        assertTrue(sub1.isActive());
        assertTrue(subGlobal.isActive());

        WayangEvent event = DefaultWayangEvent.of(
                ControlPlaneEventType.EXECUTION_STARTED,
                CorrelationId.generate(),
                Map.of("status", "RUNNING")
        );
        EventEnvelope envelope = EventEnvelope.of(event, CoordinationEpoch.of(1), EventSequence.of(1));

        bus.publish(stream, envelope);

        assertEquals(1, receivedStreamEvents.size());
        assertEquals(1, receivedGlobalEvents.size());
        assertEquals(event.correlationId(), receivedStreamEvents.get(0).event().correlationId());

        // Cancel stream subscription and publish again
        sub1.cancel();
        assertFalse(sub1.isActive());

        WayangEvent event2 = DefaultWayangEvent.of(
                ControlPlaneEventType.NODE_COMPLETED,
                CorrelationId.generate(),
                Map.of("nodeId", "node-1")
        );
        EventEnvelope envelope2 = EventEnvelope.of(event2, CoordinationEpoch.of(1), EventSequence.of(2));

        bus.publish(stream, envelope2);

        // Stream sub shouldn't receive event2, but global sub should
        assertEquals(1, receivedStreamEvents.size());
        assertEquals(2, receivedGlobalEvents.size());
    }
}
