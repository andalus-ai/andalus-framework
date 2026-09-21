package tech.kayys.andalus.spi.sandbox;

import java.time.Instant;

public record SandboxLease(
        String sandboxId,
        String ownerId,
        Instant acquiredAt,
        Instant expiresAt
) {
}
