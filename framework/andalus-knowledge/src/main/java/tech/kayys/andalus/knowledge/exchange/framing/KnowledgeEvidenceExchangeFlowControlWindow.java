package tech.kayys.andalus.knowledge.exchange.framing;

import tech.kayys.andalus.knowledge.*;
import tech.kayys.andalus.knowledge.seal.*;
import tech.kayys.andalus.knowledge.snapshot.*;
import tech.kayys.andalus.knowledge.snapshot.pack.*;
import tech.kayys.andalus.knowledge.snapshot.artifact.*;
import tech.kayys.andalus.knowledge.snapshot.merkle.*;
import tech.kayys.andalus.knowledge.exchange.*;
import tech.kayys.andalus.knowledge.exchange.auth.*;
import tech.kayys.andalus.knowledge.exchange.session.*;
import tech.kayys.andalus.knowledge.exchange.binding.*;
import tech.kayys.andalus.knowledge.exchange.envelope.*;
import tech.kayys.andalus.knowledge.exchange.trust.*;
import tech.kayys.andalus.knowledge.exchange.identity.*;
import tech.kayys.andalus.knowledge.exchange.capability.*;
import tech.kayys.andalus.knowledge.exchange.protocol.*;
import tech.kayys.andalus.knowledge.exchange.transport.*;
import tech.kayys.andalus.knowledge.exchange.framing.*;


import java.util.concurrent.atomic.AtomicLong;

/**
 * Provides knowledge evidence exchange flow control window behavior for the Wayang framework.
 */


public final class KnowledgeEvidenceExchangeFlowControlWindow {

    private final AtomicLong availableBytes;

    public KnowledgeEvidenceExchangeFlowControlWindow(
            long initialBytes
    ) {

        if (initialBytes < 0) {
            throw new IllegalArgumentException(
                    "initialBytes must be >= 0"
            );
        }

        availableBytes =
                new AtomicLong(initialBytes);
    }

    public long available() {
        return availableBytes.get();
    }

    public boolean tryConsume(long bytes) {

        if (bytes < 0) {
            throw new IllegalArgumentException(
                    "bytes must be >= 0"
            );
        }

        while (true) {

            long current =
                    availableBytes.get();

            if (current < bytes) {
                return false;
            }

            if (availableBytes.compareAndSet(
                    current,
                    current - bytes
            )) {

                return true;
            }
        }
    }

    public void update(long bytes) {

        if (bytes <= 0) {
            throw new IllegalArgumentException(
                    "bytes must be positive"
            );
        }

        availableBytes.addAndGet(bytes);
    }
}
