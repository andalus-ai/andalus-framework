package tech.kayys.andalus.execution.core.provenance;

import tech.kayys.andalus.execution.provenance.ExecutionNode;
import tech.kayys.andalus.execution.provenance.IntegrityVerifier;
import tech.kayys.andalus.execution.provenance.ProvenanceChain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

/**
 * Standard implementation of {@link IntegrityVerifier} validating temporal order and cryptographic hash chaining.
 */
public class DefaultIntegrityVerifier implements IntegrityVerifier {

    @Override
    public boolean verifyChain(ProvenanceChain chain) {
        if (chain == null || chain.nodes().isEmpty()) {
            return false;
        }

        List<ExecutionNode> nodes = chain.nodes();
        // 1. Verify chronological monotonic ordering
        for (int i = 1; i < nodes.size(); i++) {
            if (nodes.get(i).occurredAt().isBefore(nodes.get(i - 1).occurredAt())) {
                return false;
            }
        }

        // 2. Verify chain digest
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (ExecutionNode node : nodes) {
                md.update(node.id().value().getBytes());
                md.update(node.type().name().getBytes());
                md.update(node.occurredAt().toString().getBytes());
            }
            String computed = "sha256:" + HexFormat.of().formatHex(md.digest());
            return computed.equalsIgnoreCase(chain.chainDigest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static String computeDigest(List<ExecutionNode> nodes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (ExecutionNode node : nodes) {
                md.update(node.id().value().getBytes());
                md.update(node.type().name().getBytes());
                md.update(node.occurredAt().toString().getBytes());
            }
            return "sha256:" + HexFormat.of().formatHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
