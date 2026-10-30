# Repository guidance for AI coding agents

This file applies to the entire Andalus Framework repository. Read `DEVELOPMENT_GUIDELINES.md` before making architectural or public API changes.

## Repository role

- This repository owns reusable, stable contracts and infrastructure-independent agent capabilities.
- Runtime-specific implementations, Quarkus/CDI wiring, persistence adapters, transport endpoints and deployment concerns belong in `andalus` unless a documented exception exists.
- Prefer defining a small, composable SPI over coupling a reusable capability to one runtime, vendor or deployment model.

## Before changing code

1. Read the target module's README and POM; inspect source and tests.
2. Search sibling modules for an existing contract before introducing another type or abstraction.
3. Inspect downstream runtime usage before changing public interfaces, serialized types or lifecycle semantics.
4. Check dependency direction and avoid cycles or dependencies on runtime modules.
5. Make the smallest coherent change; do not perform unrelated module-wide refactors.

## Architecture rules

- Keep API/contract modules separate from default implementations where that separation is already established and useful.
- Keep framework public APIs independent of Quarkus, CDI, databases, brokers and vendor SDKs unless the module is explicitly an adapter/integration module.
- Use interfaces and immutable value types for stable contracts; document nullability, ownership, thread-safety, cancellation and error semantics.
- Avoid duplicate concepts across `andalus-core`, `andalus-spi`, `andalus-execution-api`, state, event, workflow and runtime SPI modules. Consolidate ownership through reviewed compatibility paths, not abrupt removals.
- Keep extension points explicit, typed, discoverable and versionable. Avoid reflection-based magic when a clear SPI or registry is sufficient.
- Do not let framework modules depend on the runtime repository.

## Java and Maven

- Follow the Java version and Maven configuration declared by the repository; currently the parent POM targets Java 25.
- Use `./mvnw` for builds.
- Keep dependency versions centralized in parent dependency management.
- Keep dependencies minimal and API modules especially lightweight.
- Do not add infrastructure dependencies to a contract module merely to simplify one implementation.

## Tests and compatibility

- Test contracts independently of the runtime.
- Add compatibility/conformance tests for implementations of public SPIs.
- Test lifecycle transitions, cancellation, serialization compatibility, error semantics and extension discovery where relevant.
- Treat public interfaces, events, schemas and persisted representations as compatibility-sensitive.
- Prefer additive evolution and deprecation windows. Document breaking changes and provide migration guidance.
- Do not remove or weaken tests to make the build pass.

## Documentation and completion

- Every module should explain its responsibility, public types, extension points, dependencies, examples and tests.
- Document thread-safety, lifecycle, nullability, error handling and compatibility expectations for public contracts.
- In the final change summary, list affected modules, API compatibility impact, tests actually run and any runtime migration required.
