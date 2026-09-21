package tech.kayys.andalus.knowledge.exchange.transfer;

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
import tech.kayys.andalus.knowledge.exchange.transfer.*;
import tech.kayys.andalus.knowledge.exchange.replication.*;
import tech.kayys.andalus.knowledge.exchange.sync.*;
import tech.kayys.andalus.knowledge.exchange.federation.*;
import tech.kayys.andalus.knowledge.exchange.routing.*;
import tech.kayys.andalus.knowledge.exchange.fusion.*;

/**
 * Represents a knowledge evidence transfer quota.
 *
 * <p>Its components capture `max bytes per transfer`, `max concurrent transfers`, `max bytes per runtime`, `max chunk bytes`.</p>
 *
 * @param maxBytesPerTransfer the max bytes per transfer
 * @param maxConcurrentTransfers the max concurrent transfers
 * @param maxBytesPerRuntime the max bytes per runtime
 * @param maxChunkBytes the max chunk bytes
 */



public record KnowledgeEvidenceTransferQuota(

        long maxBytesPerTransfer,

        long maxConcurrentTransfers,

        long maxBytesPerRuntime,

        int maxChunkBytes

) {

    public static KnowledgeEvidenceTransferQuota defaults() {

        return new KnowledgeEvidenceTransferQuota(
                4L * 1024L * 1024L * 1024L,
                32,
                32L * 1024L * 1024L * 1024L,
                8 * 1024 * 1024
        );
    }
}
