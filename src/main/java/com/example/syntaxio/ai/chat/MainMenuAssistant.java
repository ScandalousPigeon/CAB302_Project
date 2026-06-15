package com.example.syntaxio.ai.chat;

import com.example.syntaxio.ai.client.LLMClient;
import com.example.syntaxio.ai.safety.AiSafetyGuardrail;
import com.example.syntaxio.ai.safety.GuardrailDecision;

public class MainMenuAssistant {

    private final LLMClient llmClient;
    private final AiSafetyGuardrail safetyGuardrail;

    public MainMenuAssistant(LLMClient llmClient) {
        this.llmClient = llmClient;
        this.safetyGuardrail = new AiSafetyGuardrail();
    }

    public String reply(String userMessage) {
        GuardrailDecision decision = safetyGuardrail.assessUserMessage(userMessage);
        if (!decision.permitted()) {
            return decision.safeResponse();
        }

        String prompt = """
                You are the friendly AI assistant for Syntaxio, a beginner coding practice app.

                Help users understand programming, algorithms, and data structures.
                Keep explanations beginner-friendly.
                Do not generate full assignment solutions.

                User message:
                %s
                """.formatted(userMessage);

        String response = llmClient.generate(safetyGuardrail.applyToPrompt(prompt));
        return safetyGuardrail.safeChatResponse(response);
    }
}
