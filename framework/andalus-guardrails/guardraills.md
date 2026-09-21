
## 🛡️ **10. Guardrails Service (andalus-guardrails)**

### **Purpose**
Safety, compliance, PII detection, content moderation, and policy enforcement.

### **Project Structure**

```
andalus-guardrails/
├── pom.xml
└── src/main/java/tech/kayys/andalus/guardrails/
    ├── resource/
    │   └── GuardrailsResource.java
    ├── service/
    │   ├── GuardrailsService.java
    │   ├── PolicyEngine.java
    │   └── DetectorOrchestrator.java
    ├── detector/
    │   ├── PIIDetector.java
    │   ├── ToxicityDetector.java
    │   ├── BiasDetector.java
    │   └── HallucinationDetector.java
    ├── policy/
    │   ├── CELPolicyEvaluator.java
    │   └── PolicyRepository.java
    └── redactor/
        └── ContentRedactor.java
```
