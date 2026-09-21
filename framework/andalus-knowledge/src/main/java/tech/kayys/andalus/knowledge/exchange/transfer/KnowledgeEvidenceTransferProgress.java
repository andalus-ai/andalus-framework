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
