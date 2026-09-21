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


import java.util.Objects;

/**
 * Represents a knowledge evidence exchange protocol version.
 *
 * <p>Its components capture `major`, `minor`.</p>
 *
 * @param major the major
 * @param minor the minor
 */


public record KnowledgeEvidenceExchangeProtocolVersion(

        int major,

        int minor

) implements Comparable<KnowledgeEvidenceExchangeProtocolVersion> {

    public KnowledgeEvidenceExchangeProtocolVersion {
        if (major < 0) {
            throw new IllegalArgumentException(
                    "major must be >= 0"
            );
        }

        if (minor < 0) {
            throw new IllegalArgumentException(
                    "minor must be >= 0"
            );
        }
    }

    @Override
    public int compareTo(
            KnowledgeEvidenceExchangeProtocolVersion other
    ) {

        Objects.requireNonNull(other);

        int majorCompare =
                Integer.compare(major, other.major);

        if (majorCompare != 0) {
            return majorCompare;
        }

        return Integer.compare(minor, other.minor);
    }

    @Override
    public String toString() {
        return major + "." + minor;
    }
}
