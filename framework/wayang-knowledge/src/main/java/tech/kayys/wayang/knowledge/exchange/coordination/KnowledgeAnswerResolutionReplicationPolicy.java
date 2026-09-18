package tech.kayys.wayang.knowledge.exchange.coordination;

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
import tech.kayys.wayang.knowledge.exchange.coverage.*;
import tech.kayys.wayang.knowledge.exchange.gap.*;
import tech.kayys.wayang.knowledge.exchange.attribution.*;
import tech.kayys.wayang.knowledge.exchange.contradiction.*;
import tech.kayys.wayang.knowledge.exchange.factuality.*;
import tech.kayys.wayang.knowledge.exchange.uncertainty.*;
import tech.kayys.wayang.knowledge.exchange.compact.*;
import tech.kayys.wayang.knowledge.exchange.resolution.*;
import tech.kayys.wayang.knowledge.exchange.quorum.*;
import tech.kayys.wayang.knowledge.exchange.selection.*;
import tech.kayys.wayang.knowledge.exchange.coordination.*;
import tech.kayys.wayang.knowledge.exchange.attestation.*;
import tech.kayys.wayang.knowledge.exchange.proof.*;
import tech.kayys.wayang.knowledge.exchange.validity.*;
import tech.kayys.wayang.knowledge.exchange.lease.*;
import tech.kayys.wayang.knowledge.exchange.recovery.*;

/**
 * Represents a knowledge answer resolution replication policy.
 *
 * <p>Its components capture `minimum replicas`, `target replicas`, `require verification`, `allow remote replication`, `allow offline queue`, and other values.</p>
 *
 * @param minimumReplicas the minimum replicas
 * @param targetReplicas the target replicas
 * @param requireVerification the require verification
 * @param allowRemoteReplication the allow remote replication
 * @param allowOfflineQueue the allow offline queue
 * @param localFirst the local first
 * @param maxResolutionBytes the max resolution bytes
 */



public record KnowledgeAnswerResolutionReplicationPolicy(

        int minimumReplicas,

        int targetReplicas,

        boolean requireVerification,

        boolean allowRemoteReplication,

        boolean allowOfflineQueue,

        boolean localFirst,

        long maxResolutionBytes
) {

    public static KnowledgeAnswerResolutionReplicationPolicy
    defaults() {

        return new KnowledgeAnswerResolutionReplicationPolicy(
                1,
                2,
                true,
                true,
                true,
                true,
                4 * 1024 * 1024
        );
    }
}
