package tech.kayys.wayang.knowledge.exchange.transfer;

import tech.kayys.wayang.knowledge.*;
import tech.kayys.wayang.knowledge.seal.*;
import tech.kayys.wayang.knowledge.snapshot.*;
import tech.kayys.wayang.knowledge.snapshot.pack.*;
import tech.kayys.wayang.knowledge.snapshot.artifact.*;
import tech.kayys.wayang.knowledge.snapshot.merkle.*;
import tech.kayys.wayang.knowledge.exchange.*;
import tech.kayys.wayang.knowledge.exchange.auth.*;
import tech.kayys.wayang.knowledge.exchange.session.*;
import tech.kayys.wayang.knowledge.exchange.binding.*;
import tech.kayys.wayang.knowledge.exchange.envelope.*;
import tech.kayys.wayang.knowledge.exchange.trust.*;
import tech.kayys.wayang.knowledge.exchange.identity.*;
import tech.kayys.wayang.knowledge.exchange.capability.*;
import tech.kayys.wayang.knowledge.exchange.protocol.*;
import tech.kayys.wayang.knowledge.exchange.transport.*;
import tech.kayys.wayang.knowledge.exchange.framing.*;
import tech.kayys.wayang.knowledge.exchange.transfer.*;
import tech.kayys.wayang.knowledge.exchange.replication.*;
import tech.kayys.wayang.knowledge.exchange.sync.*;
import tech.kayys.wayang.knowledge.exchange.federation.*;
import tech.kayys.wayang.knowledge.exchange.routing.*;
import tech.kayys.wayang.knowledge.exchange.fusion.*;


import java.time.Instant;

/**
 * Represents a knowledge evidence transfer progress.
 *
 * <p>Its components capture `transfer id`, `transferred bytes`, `total bytes`, `next offset`, `next sequence`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param transferredBytes the transferred bytes
 * @param totalBytes the total bytes
 * @param nextOffset the next offset
 * @param nextSequence the next sequence
 * @param updatedAt the updated at
 */


public record KnowledgeEvidenceTransferProgress(

        String transferId,

        long transferredBytes,

        long totalBytes,

        long nextOffset,

        long nextSequence,

        Instant updatedAt

) {

    public double fraction() {

        if (totalBytes <= 0) {
            return 0.0;
        }

        return Math.min(
                1.0,
                (double) transferredBytes /
                        (double) totalBytes
        );
    }
}
