# Andalus Ecosystem Architecture

Status: proposed implementation baseline for Phase 5  
Scope: framework contracts and extension points

## Goal

Make Andalus extensible by independent agent, skill, tool, and provider authors without making the framework depend on a hosted marketplace, a particular runtime, or a specific artifact repository.

The framework owns portable metadata and SPIs. The runtime owns discovery endpoints, installation orchestration, deployment, and operational policy. A marketplace is a distribution and trust service, not an execution engine.

## Existing foundations to reuse

The repository already contains several relevant foundations:

- `AgentContract` and agent identity/capability metadata in `andalus-spi`.
- `PluginRegistry`, `PluginManager`, and plugin lifecycle abstractions.
- Skill resolution and `SkillLifecycleManager` with version-aware lifecycle semantics.
- A2A and local agent registries for protocol-specific discovery.

Do not introduce a second generic registry or a second plugin lifecycle API. Extend these contracts only when a demonstrated gap exists. Protocol registries remain protocol adapters; they are not the canonical catalog.

## Ecosystem concepts

### 1. Publishable artifact

A publishable artifact is an immutable, versioned package describing one installable unit:

- agent
- skill
- tool
- provider adapter
- workflow template
- plugin

Every artifact must have a stable identifier, semantic version, artifact kind, human-readable metadata, declared capabilities, input/output schema references, runtime/framework compatibility range, dependency declarations, license, publisher identity, and content digest.

Artifact identity is distinct from display name. A version must never be silently overwritten after publication.

### 2. Manifest and compatibility

Use a versioned, machine-readable manifest (initially JSON) as the portable package descriptor. The manifest should be validated before publication and again before installation.

Compatibility checks must cover:
- Andalus Framework API/SPI range.
- Andalus Runtime range, when runtime-specific.
- Java runtime range.
- Required protocols and capabilities.
- Dependency constraints and conflicts.

Unknown manifest fields should be tolerated where safe, while unsupported manifest major versions must be rejected.

### 3. Catalog and registry boundaries

Keep these responsibilities separate:

- Catalog: searchable metadata and artifact versions.
- Artifact store: immutable package bytes addressed by digest.
- Trust service: publisher identity, signatures, attestations, revocation.
- Local registry: artifacts known to one installation.
- Resolver: dependency and compatibility resolution.
- Lifecycle manager: install, activate, deactivate, upgrade, uninstall.
- Runtime: execute an installed artifact under policy.

A catalog result is untrusted metadata. Discovery must never implicitly install or execute an artifact.

### 4. Trust and supply chain

Before an artifact can be activated in a production runtime:

1. Verify its content digest.
2. Verify publisher signature against configured trust roots.
3. Validate manifest and schemas.
4. Resolve dependencies without allowing version substitution.
5. Evaluate permissions and requested capabilities.
6. Apply runtime policy and approval requirements.
7. Record an auditable installation decision.

Support revocation and quarantine. Signature verification establishes origin and integrity, not that an artifact is safe.

### 5. Extension author experience

Provide stable authoring guidance and a compatibility test kit before building a public marketplace UI.

An extension author should be able to:
- Define an artifact manifest.
- Implement only the relevant framework SPI.
- Run contract and compatibility tests locally.
- Package reproducibly.
- Generate an SBOM and digest.
- Sign the package.
- Publish to any conforming catalog or artifact store.

Avoid requiring authors to depend on Andalus Runtime implementation classes.

## Recommended package layout

Keep the current module layout until implementation needs are demonstrated. Candidate future modules (not a mandate to create them all now):

- Framework: manifest model, validation SPI, compatibility metadata, publisher/signature verification SPI.
- Runtime: catalog client, artifact resolver, installer orchestration, policy integration, local installed-artifact inventory, CLI commands.
- External service: catalog API, immutable artifact storage, publisher accounts, moderation and revocation.

The framework must not depend on the hosted catalog or runtime implementation.

## Non-goals for the first iteration

- No public marketplace UI.
- No automatic remote code execution.
- No implicit installation during agent discovery.
- No arbitrary dependency downloads during activation.
- No central service requirement for local development.
- No new registry abstraction that duplicates `PluginRegistry`, skill lifecycle, or protocol registries.

## Acceptance criteria

- An extension can be described without importing runtime classes.
- A manifest can be validated offline.
- Artifact identity and version are immutable.
- Discovery is read-only and separate from installation.
- Installation is separate from activation and execution.
- Compatibility, permissions, digest, and signature checks are explicit.
- Local development works without a hosted marketplace.
- Existing plugin, skill, and agent contracts remain the source of truth unless an ADR justifies a change.
