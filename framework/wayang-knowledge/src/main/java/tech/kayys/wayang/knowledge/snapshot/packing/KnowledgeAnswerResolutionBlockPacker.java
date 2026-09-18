package tech.kayys.wayang.knowledge.snapshot.packing;

import java.util.List;

/**
 * Defines the contract for knowledge answer resolution block packer operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionBlockPacker {

    List<KnowledgeAnswerResolutionPackedStateBlock> pack(
            List<KnowledgeAnswerResolutionLogicalStateBlock> logicalBlocks
    );
}
