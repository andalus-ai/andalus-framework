# Wayang Framework

> **A modular, extensible Agentic Runtime Framework for building autonomous AI agents and workflows.**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://openjdk.org/)

**Author:** Bhangun  
**Organization:** [kayys.tech](https://kayys.tech)

---

## 🧭 Overview

**Wayang Framework** is the foundational, protocol-neutral toolkit for building agentic systems. It provides the core abstractions, SPIs, and reusable building blocks that power the **Wayang Runtime**.

The framework was deliberately **split** from the main Wayang runtime to establish a clean separation of concerns:

- **`wayang-framework`** (this repository) — Reusable, standalone libraries. Framework contracts, adapters, tools, and domain-agnostic services.
- **`wayang`** (runtime) — The opinionated, ready-to-run agent execution engine, built on top of the framework.

This separation allows the framework to evolve independently, be embedded in other runtimes, and serve as a stable foundation for the broader Wayang ecosystem.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Wayang Runtime                              │
│                      (github.com/bhangun/wayang)                    │
│                                                                     │
│   Agent Execution • Orchestration • CLI • REST API • Deployment     │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Wayang Framework                               │
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌────────────┐  │
│  │   Core      │  │  Protocol   │  │    Tool     │  │  Knowledge │  │
│  │  Contracts  │  │  Adapters   │  │   Systems   │  │   Systems  │  │
│  │             │  │  (A2A/ANP)  │  │             │  │            │  │
│  │  Agent      │  │             │  │  Builtin    │  │  Vector    │  │
│  │  Skill      │  │  Local      │  │  Tools      │  │  Graph     │  │
│  │  Tool       │  │  A2A        │  │  MCP Client │  │  RAG       │  │
│  │  Workflow   │  │  ANP        │  │             │  │  Memory    │  │
│  │  Memory     │  │             │  │             │  │            │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └────────────┘  │
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌────────────┐  │
│  │  Security   │  │  Resilience │  │Observability│  │ Governance │  │
│  │             │  │             │  │             │  │            │  │
│  │  AuthN/AuthZ│  │  Circuit    │  │  Telemetry  │  │ Guardrails │  │
│  │  Delegation │  │  Breaker    │  │  Tracing    │  │ HITL       │  │
│  │  Obligation │  │  Retry      │  │  Audit      │  │ Policy     │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         External Systems                            │
│                                                                     │
│   LLM Providers • MCP Servers • Vector DBs • Graph DBs • Tools      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📦 Modules

### Core Contracts & SPIs

| Module | Description |
|--------|-------------|
| `wayang-core` | Core interfaces: `Agent`, `AgentDefinition`, `AgentContext`, `AgentRequest/Response` |
| `wayang-extension` | `Extension`, `Metadata`, `Resource`, `ResourceId`, `Version` — the universal resource model |
| `wayang-common` | Shared utilities: `ErrorCode`, `ValidationResult`, `Migration` |
| `wayang-spi` | Service Provider Interfaces: `Agent`, `Skill`, `Tool`, `Memory`, `Sandbox`, `Plugin`, `Capability` |

### Execution & Orchestration

| Module | Description |
|--------|-------------|
| `wayang-agent` | ReAct, PlanAndSolve, Reflection, Research agents + `AgentOrchestrator` |
| `wayang-engine` | Execution engine: `ExecutionGraph`, `ExecutionNode`, DAG scheduling |
| `wayang-workflow` | Workflow definitions, steps, transitions, `WorkflowEngine` |
| `wayang-execution-api` | `ExecutionPipeline`, `AgentInvocation`, interceptors, cancellation |
| `wayang-execution-core` | Default pipeline implementation, chain, context, tokens |

### Communication & Protocols

| Module | Description |
|--------|-------------|
| `wayang-communication-api` | Protocol-neutral `AgentCommunicator`, `AgentRequest/Response`, endpoints |
| `wayang-communication-core` | Default communicator, protocol routing, selection policy |
| `wayang-protocol-local` | In-process local agent communication |
| `wayang-a2a` | Agent-to-Agent Protocol (A2A) adapter, agent cards, durable tasks |
| `wayang-anp` | Agent Network Protocol (ANP) adapter, DID:WBA, discovery |
| `wayang-agent-network-api` | Distributed Agent Network contracts |
| `wayang-agent-network-core` | Network client, protocol registry, local implementation |

### Tools & Extensions

| Module | Description |
|--------|-------------|
| `wayang-tool` | Tool SPI: `Tool`, `ToolDescriptor`, `ToolResult`, `ToolRegistry` |
| `wayang-mcp` | Model Context Protocol (MCP) client — HTTP/SSE + STDIO |
| `wayang-builtin-tools` | Built-in tools: CodeGrep, CodeScanner, Planner, TaskStore |
| `wayang-plugin` | Plugin system: manifests, dependency resolution, lifecycle, capability registry |
| `wayang-command` | Command discovery API for workbench & command palette |

### Knowledge & Memory

| Module | Description |
|--------|-------------|
| `wayang-knowledge` | Domain-neutral knowledge: items, evidence, provenance, decisions, lineage |
| `wayang-rag` | RAG SPI: chunking, embedding, vector store, retrieval strategy, pipeline |
| `wayang-vector` | Vector store contracts and embedding provider SPIs |
| `wayang-graph` | Graph store SPI for knowledge graphs |
| `wayang-memory` | Memory management: policies, scopes, snapshots, hierarchy visualization |

### Security & Governance

| Module | Description |
|--------|-------------|
| `wayang-security-api` | AuthN/AuthZ SPI, `SecurityContext`, `DelegationContext`, obligations, PEP |
| `wayang-security-core` | Default implementations: policy engine, delegation, PEP, audit sink |
| `wayang-governance` | `Evaluator`, `GuardrailProvider`, policy assessment |
| `wayang-guardrails` | PII, Toxicity, Bias, Hallucination detectors + plugin system |
| `wayang-hitl` | Human-in-the-Loop: task lifecycle, escalation, approvals |
| `wayang-audit` | Audit events, queries, statistics |
| `wayang-error-spi` | Centralized `ErrorCode` registry with JAX-RS mappers |

### Resilience & Caching

| Module | Description |
|--------|-------------|
| `wayang-resilience` | `CircuitBreaker`, `RetryPolicy`, `RetryTemplate`, rate limiting |
| `wayang-cache` | Cache keys, policies, stores, invalidation |
| `wayang-embedding` | Embedding service: batch pipeline, caching, tenant strategies |

### Observability

| Module | Description |
|--------|-------------|
| `wayang-observability` | Health checks, metrics, telemetry, tracing service |
| `wayang-observability-otel` | OpenTelemetry integration, `OtelAgentListener` |

### Context & Knowledge Compilation

| Module | Description |
|--------|-------------|
| `wayang-context` | Three-tier context compiler: full source → skeleton → digest, symbol resolution |

### Providers & Configuration

| Module | Description |
|--------|-------------|
| `wayang-provider` | `Provider`, `ModelRouter` (Direct/Adaptive), model catalog, streaming events |
| `wayang-configuration` | Configuration as a resource: sources, watchers, registry |
| `wayang-schemas` | JSON schema builder, registry, domain handlers |
| `wayang-registry` | Standard registry (A2A, A2UI, Agentic Commerce), drift detection |

### Project & Session

| Module | Description |
|--------|-------------|
| `wayang-project` | `Project`, `Session`, `ProjectStore`, persistence strategies |
| `wayang-research` | Research artifacts, sessions, steps, caching |
| `wayang-harness` | Harness SPI: `Harness`, `HarnessRequest/Result/Context` |

### Support

| Module | Description |
|--------|-------------|
| `wayang-schemas` | JSON utilities (`Json`, `JsonValue`), schema registry |
| `wayang-shaker` | Tree-shaking, agent packaging, workflow analysis |
| `wayang-messaging` | Message queue service SPI |
| `wayang-embedding` | Embedding generation, batch pipeline, caching |

---

## 🚀 Relationship to Wayang Runtime

| Aspect | `wayang-framework` | `wayang` (runtime) |
|--------|--------------------|--------------------|
| **Purpose** | Reusable, embeddable libraries | Executable, opinionated runtime |
| **Focus** | Contracts, SPIs, adapters | Orchestration, deployment, CLI, API |
| **Dependencies** | Minimal, protocol-neutral | Depends on framework modules |
| **Use Case** | Build custom agentic systems | Run Wayang agents out-of-the-box |
| **Artifact** | Libraries (JARs) | Application / service |

**Key insight:** The framework provides *what* an agentic system needs. The runtime provides *how* it executes, deploys, and integrates.

---

## 🔧 Getting Started

### Prerequisites

- **Java 25+**
- **Maven 3.9+**

### Build the Framework

```bash
git clone https://github.com/bhangun/wayang-framework.git
cd wayang-framework
mvn clean install
```

### Use as a Dependency

```xml
<dependency>
    <groupId>tech.kayys.wayang</groupId>
    <artifactId>wayang-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Example: Define a Tool

```java
public class MyTool implements Tool {
    @Override
    public ToolDescriptor descriptor() {
        return SimpleToolDescriptor.of(
            "my-tool",
            "A custom tool",
            Map.of("type", "object")
        );
    }

    @Override
    public CompletableFuture<ToolResult> execute(
            ToolInvocation invocation, ToolContext context) {
        return CompletableFuture.completedFuture(
            SimpleToolResult.success(Map.of("result", "done"))
        );
    }
}
```

### Example: Build an Agent

```java
ReActAgent agent = new ReActAgent();
agent.setProvider(myProvider);
agent.setModelId("gpt-4");
agent.setTools(List.of(new MyTool()));
agent.setMemory(new ConversationMemory(50));

agent.send("What's the weather?", listener);
```

---

## 📚 Documentation

Detailed module documentation is available in each module's `README.md` and via Javadoc.

Key topics:
- [Agent Lifecycle & Execution](wayang-agent/README.md)
- [Protocol Adapters (A2A, ANP)](wayang-a2a/README.md)
- [Tool System & MCP](wayang-tool/README.md)
- [Knowledge & RAG](wayang-knowledge/README.md)
- [Security & Governance](wayang-security-api/README.md)
- [Resilience Patterns](wayang-resilience/README.md)

---

## 🤝 Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---



---

## 🔗 Links

- **Runtime Repository:** [github.com/bhangun/wayang](https://github.com/bhangun/wayang)
- **Organization:** [kayys.tech](https://kayys.tech)
- **Issues:** [github.com/bhangun/wayang-framework/issues](https://github.com/bhangun/wayang-framework/issues)

---

## 🙏 Acknowledgments

Built with ❤️ by [Bhangun](https://github.com/bhangun) at [kayys.tech](https://kayys.tech).



