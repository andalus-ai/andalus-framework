# Andalus Framework

> **A modular, extensible Agentic Runtime Framework for building autonomous AI agents and workflows.**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://openjdk.org/)

**Author:** Bhangun  
**Organization:** [kayys.tech](https://kayys.tech)

---

## 🧭 Overview

**Andalus Framework** is the foundational, protocol-neutral toolkit for building agentic systems. It provides the core abstractions, SPIs, and reusable building blocks that power the **Andalus Runtime**.

The framework was deliberately **split** from the main Andalus runtime to establish a clean separation of concerns:

- **`andalus-framework`** (this repository) — Reusable, standalone libraries. Framework contracts, adapters, tools, and domain-agnostic services.
- **`andalus`** (runtime) — The opinionated, ready-to-run agent execution engine, built on top of the framework.

This separation allows the framework to evolve independently, be embedded in other runtimes, and serve as a stable foundation for the broader Andalus ecosystem.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         Andalus Runtime                              │
│                      (github.com/bhangun/andalus)                    │
│                                                                     │
│   Agent Execution • Orchestration • CLI • REST API • Deployment     │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      Andalus Framework                               │
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
| `andalus-core` | Core interfaces: `Agent`, `AgentDefinition`, `AgentContext`, `AgentRequest/Response` |
| `andalus-extension` | `Extension`, `Metadata`, `Resource`, `ResourceId`, `Version` — the universal resource model |
| `andalus-common` | Shared utilities: `ErrorCode`, `ValidationResult`, `Migration` |
| `andalus-spi` | Service Provider Interfaces: `Agent`, `Skill`, `Tool`, `Memory`, `Sandbox`, `Plugin`, `Capability` |

### Execution & Orchestration

| Module | Description |
|--------|-------------|
| `andalus-agent` | ReAct, PlanAndSolve, Reflection, Research agents + `AgentOrchestrator` |
| `andalus-engine` | Execution engine: `ExecutionGraph`, `ExecutionNode`, DAG scheduling |
| `andalus-workflow` | Workflow definitions, steps, transitions, `WorkflowEngine` |
| `andalus-execution-api` | `ExecutionPipeline`, `AgentInvocation`, interceptors, cancellation |
| `andalus-execution-core` | Default pipeline implementation, chain, context, tokens |

### Communication & Protocols

| Module | Description |
|--------|-------------|
| `andalus-communication-api` | Protocol-neutral `AgentCommunicator`, `AgentRequest/Response`, endpoints |
| `andalus-communication-core` | Default communicator, protocol routing, selection policy |
| `andalus-protocol-local` | In-process local agent communication |
| `andalus-a2a` | Agent-to-Agent Protocol (A2A) adapter, agent cards, durable tasks |
| `andalus-anp` | Agent Network Protocol (ANP) adapter, DID:WBA, discovery |
| `andalus-agent-network-api` | Distributed Agent Network contracts |
| `andalus-agent-network-core` | Network client, protocol registry, local implementation |

### Tools & Extensions

| Module | Description |
|--------|-------------|
| `andalus-tool` | Tool SPI: `Tool`, `ToolDescriptor`, `ToolResult`, `ToolRegistry` |
| `andalus-mcp` | Model Context Protocol (MCP) client — HTTP/SSE + STDIO |
| `andalus-builtin-tools` | Built-in tools: CodeGrep, CodeScanner, Planner, TaskStore |
| `andalus-plugin` | Plugin system: manifests, dependency resolution, lifecycle, capability registry |
| `andalus-command` | Command discovery API for workbench & command palette |

### Knowledge & Memory

| Module | Description |
|--------|-------------|
| `andalus-knowledge` | Domain-neutral knowledge: items, evidence, provenance, decisions, lineage |
| `andalus-rag` | RAG SPI: chunking, embedding, vector store, retrieval strategy, pipeline |
| `andalus-vector` | Vector store contracts and embedding provider SPIs |
| `andalus-graph` | Graph store SPI for knowledge graphs |
| `andalus-memory` | Memory management: policies, scopes, snapshots, hierarchy visualization |

### Security & Governance

| Module | Description |
|--------|-------------|
| `andalus-security-api` | AuthN/AuthZ SPI, `SecurityContext`, `DelegationContext`, obligations, PEP |
| `andalus-security-core` | Default implementations: policy engine, delegation, PEP, audit sink |
| `andalus-governance` | `Evaluator`, `GuardrailProvider`, policy assessment |
| `andalus-guardrails` | PII, Toxicity, Bias, Hallucination detectors + plugin system |
| `andalus-hitl` | Human-in-the-Loop: task lifecycle, escalation, approvals |
| `andalus-audit` | Audit events, queries, statistics |
| `andalus-error-spi` | Centralized `ErrorCode` registry with JAX-RS mappers |

### Resilience & Caching

| Module | Description |
|--------|-------------|
| `andalus-resilience` | `CircuitBreaker`, `RetryPolicy`, `RetryTemplate`, rate limiting |
| `andalus-cache` | Cache keys, policies, stores, invalidation |
| `andalus-embedding` | Embedding service: batch pipeline, caching, tenant strategies |

### Observability

| Module | Description |
|--------|-------------|
| `andalus-observability` | Health checks, metrics, telemetry, tracing service |
| `andalus-observability-otel` | OpenTelemetry integration, `OtelAgentListener` |

### Context & Knowledge Compilation

| Module | Description |
|--------|-------------|
| `andalus-context` | Three-tier context compiler: full source → skeleton → digest, symbol resolution |

### Providers & Configuration

| Module | Description |
|--------|-------------|
| `andalus-provider` | `Provider`, `ModelRouter` (Direct/Adaptive), model catalog, streaming events |
| `andalus-configuration` | Configuration as a resource: sources, watchers, registry |
| `andalus-schemas` | JSON schema builder, registry, domain handlers |
| `andalus-registry` | Standard registry (A2A, A2UI, Agentic Commerce), drift detection |

### Project & Session

| Module | Description |
|--------|-------------|
| `andalus-project` | `Project`, `Session`, `ProjectStore`, persistence strategies |
| `andalus-research` | Research artifacts, sessions, steps, caching |
| `andalus-harness` | Harness SPI: `Harness`, `HarnessRequest/Result/Context` |

### Support

| Module | Description |
|--------|-------------|
| `andalus-schemas` | JSON utilities (`Json`, `JsonValue`), schema registry |
| `andalus-shaker` | Tree-shaking, agent packaging, workflow analysis |
| `andalus-messaging` | Message queue service SPI |
| `andalus-embedding` | Embedding generation, batch pipeline, caching |

---

## 🚀 Relationship to Andalus Runtime

| Aspect | `andalus-framework` | `andalus` (runtime) |
|--------|--------------------|--------------------|
| **Purpose** | Reusable, embeddable libraries | Executable, opinionated runtime |
| **Focus** | Contracts, SPIs, adapters | Orchestration, deployment, CLI, API |
| **Dependencies** | Minimal, protocol-neutral | Depends on framework modules |
| **Use Case** | Build custom agentic systems | Run Andalus agents out-of-the-box |
| **Artifact** | Libraries (JARs) | Application / service |

**Key insight:** The framework provides *what* an agentic system needs. The runtime provides *how* it executes, deploys, and integrates.

---

## 🔧 Getting Started

### Prerequisites

- **Java 25+**
- **Maven 3.9+**

### Build the Framework

```bash
git clone https://github.com/bhangun/andalus-framework.git
cd andalus-framework
mvn clean install
```

### Use as a Dependency

```xml
<dependency>
    <groupId>tech.kayys.andalus</groupId>
    <artifactId>andalus-core</artifactId>
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
- [Agent Lifecycle & Execution](andalus-agent/README.md)
- [Protocol Adapters (A2A, ANP)](andalus-a2a/README.md)
- [Tool System & MCP](andalus-tool/README.md)
- [Knowledge & RAG](andalus-knowledge/README.md)
- [Security & Governance](andalus-security-api/README.md)
- [Resilience Patterns](andalus-resilience/README.md)

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

- **Runtime Repository:** [github.com/bhangun/andalus](https://github.com/bhangun/andalus)
- **Organization:** [kayys.tech](https://kayys.tech)
- **Issues:** [github.com/bhangun/andalus-framework/issues](https://github.com/bhangun/andalus-framework/issues)

---

## 🙏 Acknowledgments

Built with ❤️ by [Bhangun](https://github.com/bhangun) at [kayys.tech](https://kayys.tech).



