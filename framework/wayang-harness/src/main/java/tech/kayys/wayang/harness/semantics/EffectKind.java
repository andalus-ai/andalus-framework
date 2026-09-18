package tech.kayys.wayang.harness.semantics;

/**
 * Elementary classification of side effects an operation may produce.
 */
public enum EffectKind {
    PURE,
    READ,
    WRITE,
    CREATE,
    DELETE,
    MODIFY,
    EXECUTE,
    NETWORK,
    PROCESS,
    COMMUNICATION,
    DELEGATION,
    EXTERNAL,
    IRREVERSIBLE
}
