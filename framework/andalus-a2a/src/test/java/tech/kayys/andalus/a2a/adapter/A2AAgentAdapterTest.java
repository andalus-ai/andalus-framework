package tech.kayys.andalus.a2a.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tech.kayys.andalus.a2a.model.A2AMessage;
import tech.kayys.andalus.a2a.model.A2APart;
import tech.kayys.andalus.a2a.model.A2ATask;
import tech.kayys.andalus.a2a.model.A2ATaskStatus;
import tech.kayys.andalus.agent.AgentRequest;
import tech.kayys.andalus.agent.AgentResponse;
import tech.kayys.andalus.core.AgentDefinition;
import tech.kayys.andalus.core.runtime.AndalusRuntime;

@ExtendWith(MockitoExtension.class)
class A2AAgentAdapterTest {

    @Mock
    private AndalusRuntime mockRuntime;

    @Test
    void testSendMessage() throws Exception {
        AgentDefinition def = AgentDefinition.builder()
            .metadata(tech.kayys.andalus.extension.Metadata.builder().name("TestAgent").build())
            .build();
            
        A2AAgentAdapter adapter = new A2AAgentAdapter(mockRuntime, def);
        
        CompletableFuture<AgentResponse> futureResponse = new CompletableFuture<>();
        when(mockRuntime.executeAsync(eq(def), any(AgentRequest.class))).thenReturn(futureResponse);
        
        A2AMessage msg = new A2AMessage(
            A2AMessage.Role.USER,
            List.of(new A2APart.Text("Hello!"))
        );
        
        A2ATask initialTask = adapter.sendMessage(msg).toCompletableFuture().get();
        
        assertNotNull(initialTask.taskId());
        assertEquals(A2ATaskStatus.RUNNING, initialTask.status());
        
        // Resolve future
        futureResponse.complete(AgentResponse.success("Hello from Agent!"));
        
        A2ATask completedTask = adapter.getTask(initialTask.taskId()).toCompletableFuture().get();
        assertEquals(A2ATaskStatus.COMPLETED, completedTask.status());
        assertEquals("Hello from Agent!", completedTask.metadata().get("responseContent"));
    }
}
