# M0 — Andalus Foundation Audit and Implementation Plan

- **Status:** In progress; source-level findings recorded, repository-wide verification pending.
- **Scope:** `andalus-framework` first, with downstream compatibility checks against `andalus`.
- **Decision:** Do not introduce a new generic domain-extension module, plugin manager, agent registry, or delegation API during M0.
- **Purpose:** Establish one coherent, portable foundation that independent domain products can embed or connect to without making Andalus domain-aware.

## 1. Target architecture and ownership

| Layer | Owns | Must not own |
|---|---|---|
| Andalus Framework | Portable agent/tool/skill/workflow contracts, extension SPIs, execution semantics, reusable orchestration, protocol-neutral metadata | Runtime deployment, product-specific business workflows, tenant authority, hosted marketplace |
| Andalus Runtime | Runtime composition, concrete lifecycle implementations, policy enforcement, persistence, transport endpoints, deployment and operations | Cifr/Farabi domain semantics or canonical product state |
| Domain product (e.g. Cifr/Farabi) | Domain agent loop, domain tools/use cases, domain authorization, domain state and artifacts | Generic runtime infrastructure that should be reusable |
| Interoperability adapters | MCP/A2A/HTTP/gRPC boundary translation | Canonical internal domain model |

The same product may be integrated as selected tools, as a specialist agent, or as a complete independently running application. These are complementary surfaces, not mutually exclusive product types.

## 2. Verified source-level findings

These findings come from inspecting the current default-branch files listed below. They are not a complete repository-wide reference graph and do not establish a successful build.

### 2.1 Existing foundations to reuse

- `docs/ECOSYSTEM_ARCHITECTURE.md` already states that the framework owns portable metadata/SPIs and runtime owns discovery endpoints, installation orchestration and deployment.
- `andalus-spi`, `andalus-plugin-spi`, `andalus-extension`, `andalus-plugin`, `andalus-registry`, `andalus-agent`, `andalus-tool`, `andalus-execution-api`, `andalus-execution-core`, `andalus-communication-api`, `andalus-protocol-local`, `andalus-a2a` and `andalus-mcp` already provide relevant building blocks.
- Existing `PluginRegistry`, `PluginManager`, plugin lifecycle, agent metadata and protocol-specific registries should be evaluated and composed rather than replaced with another generic registry.
- Existing execution and checkpoint overlap is already documented in `CONTRACT_INVENTORY.md`, `CONTRACT_CONSUMER_MAP.md`, ADR-0001 and ADR-0002. Continue those migration paths; do not add competing APIs.

### 2.2 Concrete inconsistencies and risks

| Finding | Evidence inspected | Required action |
|---|---|---|
| Plugin SPI artifact has runtime/framework dependencies | `framework/andalus-plugin/pom.xml` declares Quarkus Core, Mutiny and JBoss Logging | Determine which types actually require these dependencies. Move implementation/logging/CDI concerns to an implementation adapter where possible; keep portable contracts framework-neutral. |
| Base SPI also carries reactive and serialization dependencies | `framework/andalus-spi/pom.xml` declares Mutiny, Jackson databind and YAML, and depends on `andalus-extension` | Map public signatures and usages. Split only if dependency analysis proves these are not foundational contract requirements; avoid speculative module proliferation. |
| Java baseline is inconsistent in documentation | Root `pom.xml` sets `maven.compiler.release=25`; README advertises Java 21+ | Choose and test the actual supported baseline. Align README, parent properties, module compiler properties, CI and runtime compatibility matrix. |
| Parent build imports Quarkus BOM globally | Root `pom.xml` imports `quarkus-bom` | Decide whether this is acceptable dependency management only or causes unnecessary coupling. Prefer BOM imports in implementation modules or an optional integration BOM if portable consumers should not inherit Quarkus version management. |
| Duplicate execution-context and checkpoint contracts | `CONTRACT_INVENTORY.md`, `CONTRACT_CONSUMER_MAP.md`, ADR-0001 and ADR-0002 | Complete FQN/import/implementation/call-site mapping across Framework and Runtime before coordinated migration. |
| Execution lifecycle contract mismatch | `CONTRACT_CONSUMER_MAP.md` reports `DefaultExecutionContext.transition` writes lifecycle state without validating transitions despite documented expectations | Add a transition conformance test and enforce the approved transition table in the implementation. |
| Existing extension architecture is broader than a simple plugin loader | `docs/ECOSYSTEM_ARCHITECTURE.md` separates catalog, artifact store, trust, local registry, resolver, lifecycle and runtime | Preserve those boundaries. Marketplace/distribution is not part of the initial embedded extension lifecycle. |

### 2.3 Important precision

- The inspected POM proves declared dependencies, not the exact transitive dependency impact of every published artifact.
- The duplicate FQNs and checkpoint overlaps are documented in existing ADRs; this audit does not claim to have completed all consumer searches.
- The Java 25 compiler release and Java 21+ README badge are a documentation/build configuration inconsistency. The minimum supported JDK still requires CI and source/API compatibility verification.
- No Maven build, test suite, dependency tree or runtime integration test was executed as part of this source-only audit.

## 3. Canonical responsibilities to settle in M0

### Agent and extension model

Reuse existing contracts and decide one authoritative path for:
- Agent identity and descriptor metadata.
- Capability declarations and discovery.
- Extension identity, version, compatibility and lifecycle.
- Registration and invocation.
- Tool discovery and invocation.
- Failure, timeout and shutdown behavior.

Do not conflate a plugin (installable/lifecycle-managed extension package), an agent (executable specialist), and a tool (bounded operation). A plugin may contribute agents/tools/providers, but these concepts should not collapse into one type.

### Execution and delegation

The framework should define portable invocation semantics; runtime supplies concrete execution and persistence. The canonical invocation contract must define:
- Stable execution and correlation identifiers.
- Trusted principal/tenant context supplied by the host, not by model prompts.
- Allowed capabilities and policy context.
- Deadline, resource budget and cancellation.
- Typed input/output and structured failures.
- Progress/event semantics.
- Idempotency and retry expectations.
- Artifact references and terminal outcome.
- Checkpoint/resume semantics where supported.

A specialist domain agent owns its internal domain loop. Andalus coordinates a bounded invocation and observes progress/outcome; it does not micromanage every domain operation.

### Registry and discovery

Keep separate registries where their semantics differ (plugins, local agents, A2A peers, MCP tools). Define the canonical metadata contract and explicit projections/adapters. Discovery is not authorization, installation, or activation.

## 4. Required M0 audit work

- [ ] Enumerate all modules from the parent and nested POMs; record artifact IDs and source roots.
- [ ] Generate the Maven reactor and dependency graph; identify cycles, unused modules, duplicate dependencies and unexpected framework/runtime dependencies.
- [ ] Run repository-wide search for duplicate public FQNs, descriptor types, plugin/extension managers, registry contracts, execution contexts and checkpoint stores.
- [ ] Map every public contract to implementations, consumers, tests, CDI registrations, ServiceLoader entries and serialization boundaries.
- [ ] Inspect the runtime repository's Framework version pins, Java baseline, dependency tree, public API consumers and runtime-specific adapters.
- [ ] Verify lifecycle, cancellation, timeout, error, retry, idempotency and checkpoint semantics from implementation and tests.
- [ ] Identify published artifact/version compatibility and whether any consumers require binary compatibility.
- [ ] Produce an approved contract ownership matrix and migration sequence.
- [ ] Record actual build/test commands and results; do not infer success from source inspection.

## 5. M0 deliverables

1. This audit, updated with evidence and completed checkboxes.
2. Module inventory and dependency-direction map.
3. Contract ownership and consumer/implementation map.
4. Framework ↔ Runtime compatibility matrix (JDK, framework version, Quarkus/BOM, transport and artifact versions).
5. ADR decisions for descriptor/capability ownership, extension lifecycle, and execution invocation/delegation boundary where existing ADRs do not already decide them.
6. A sequenced M1 implementation plan with acceptance tests and migration compatibility policy.

## 6. M1 implementation direction (provisional until M0 evidence is complete)

1. Consolidate existing portable contracts and lifecycle semantics; avoid new generic module names unless a clear ownership gap remains.
2. Make extension registration/discovery deterministic and version-aware using existing extension/plugin contracts.
3. Separate portable SPIs from Quarkus/CDI-specific lifecycle wiring where dependency inspection justifies it.
4. Add framework-level conformance tests for extension lifecycle, agent/tool registration, invocation context propagation, cancellation, failures and shutdown.
5. Publish a minimal extension authoring example that works without a hosted marketplace or mandatory MCP.
6. Keep MCP/A2A as adapters and preserve direct in-process Java invocation for first-party integrations.

## 7. Deployment strategy (later milestones)

| Mode | Mechanism | When to use |
|---|---|---|
| Embedded | Direct Java contracts and selected framework modules | Local/community/single-process use where JDK and dependency compatibility are proven |
| Local sidecar | Local IPC, stdio, gRPC or HTTP | Process isolation or independent lifecycle is needed |
| Remote | Versioned HTTP/gRPC APIs with streaming and cancellation | Team/Pro/enterprise and independently scaled workers |
| Interoperability | MCP/A2A adapters | External ecosystems and third-party clients |

The deployment mode must not change domain ownership or the semantic contract. Do not require Kubernetes, a broker or a hosted control plane for local use.

## 8. Exit criteria

M0 is complete only when the module/dependency and consumer maps are evidence-backed, Framework/Runtime compatibility is tested, ownership decisions are approved, and M1 can proceed without inventing a parallel abstraction system.

M1 is not authorized to make broad breaking changes until the migration plan, release compatibility policy and required runtime changes are explicit.
