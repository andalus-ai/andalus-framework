# Wayang Audit

The `wayang-audit` module defines the audit service abstraction used to record,
query, and summarize agent activity. It contains audit events, providers,
queries, results, statistics, and the service contract.

Implement `AuditProvider` for the persistence system used by an application,
then expose it through `AuditService`. Keep sensitive payload handling and
retention policy in the provider or surrounding governance layer.
