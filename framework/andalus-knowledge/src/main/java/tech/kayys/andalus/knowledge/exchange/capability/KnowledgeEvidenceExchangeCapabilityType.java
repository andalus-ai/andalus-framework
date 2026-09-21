package tech.kayys.andalus.knowledge.exchange.capability;

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

/**
 * Defines the knowledge evidence exchange capability type values used by the Wayang framework.
 */



public enum KnowledgeEvidenceExchangeCapabilityType {

    EVIDENCE_EXCHANGE,

    ARTIFACT_RESOLUTION,

    MANIFEST_RETRIEVAL,

    RESOURCE_RETRIEVAL,

    MERKLE_PROOF,

    PARTIAL_VERIFICATION,

    INTEGRITY_VERIFICATION,

    SECURE_SEAL_VERIFICATION,

    SNAPSHOT_VERIFICATION,

    SNAPSHOT_REPLAY,

    STREAMING,

    COMPRESSION,

    MCP,

    REMOTE_KNOWLEDGE,

    GOVERNED_RETRIEVAL
}
