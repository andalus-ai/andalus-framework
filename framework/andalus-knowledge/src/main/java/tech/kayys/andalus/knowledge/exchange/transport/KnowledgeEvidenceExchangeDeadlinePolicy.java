package tech.kayys.andalus.knowledge.exchange.transport;

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


import java.time.Duration;

/**
 * Represents a knowledge evidence exchange deadline policy.
 *
 * <p>Its components capture `default deadline`, `maximum deadline`.</p>
 *
 * @param defaultDeadline the default deadline
 * @param maximumDeadline the maximum deadline
 */


public record KnowledgeEvidenceExchangeDeadlinePolicy(

        Duration defaultDeadline,

        Duration maximumDeadline

) {

    public static KnowledgeEvidenceExchangeDeadlinePolicy
    defaults() {

        return new KnowledgeEvidenceExchangeDeadlinePolicy(
                Duration.ofSeconds(30),
                Duration.ofMinutes(10)
        );
    }

    public Duration normalize(Duration requested) {

        if (requested == null) {
            return defaultDeadline;
        }

        if (requested.isNegative() ||
                requested.isZero()) {

            throw new IllegalArgumentException(
                    "Deadline must be positive"
            );
        }

        return requested.compareTo(maximumDeadline) > 0
                ? maximumDeadline
                : requested;
    }
}
