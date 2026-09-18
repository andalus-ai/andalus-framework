# Wayang Event

The `wayang-event` module defines the common event model used to publish
agentic domain activity. Events contain metadata, source information, typed
payloads, and messages; domain specializations cover agents, knowledge, LLM,
memory, policy, runtime, skills, tools, and workflows.

Create domain-specific events using the shared base event and metadata types,
then route them through the event infrastructure selected by the host
application.
